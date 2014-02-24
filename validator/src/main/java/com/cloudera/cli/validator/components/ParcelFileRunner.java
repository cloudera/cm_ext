// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cloudera.cli.validator.components;

import com.cloudera.cli.validator.ValidationRunner;
import com.cloudera.common.Parser;
import com.cloudera.parcel.descriptors.AlternativeDescriptor;
import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.parcel.descriptors.PermissionDescriptor;
import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This {@link #ValidationRunner} validates a complete parcel file.
 *
 * It validates the parcel's filename as well as the contents of the parcel,
 * ensuring consistency between the filename, directory and contained metadata.
 */
@Component
public class ParcelFileRunner implements ValidationRunner {
  /**
   * This pattern defines the expected form for a regular parcel package.
   *   [product]-[version]-[distro].parcel
   * The group for [version] is greedy so that it captures embedded '-'s.
   * The [product] and [distro] are not allowed to have embedded '-'s.
   */
  private static final Pattern PARCEL_PATTERN = Pattern.compile("^(.*?)-(.*)-(.*?)\\.parcel$");

  // This is the set of distros that CM is currently aware of. Future versions
  // of CM may understand more, and this list should be updated accordingly.
  private static final Set<String> KNOWN_DISTROS = ImmutableSet.of(
      "el5",
      "el6",
      "sles11",
      "lucid",
      "precise",
      "squeeze",
      "wheezy");

  private static final String PARCEL_JSON_PATH = "/meta/parcel.json";
  private static final String ALTERNATIVES_JSON_PATH = "/meta/alternatives.json";
  private static final String PERMISSIONS_JSON_PATH = "/meta/permissions.json";

  @Autowired
  @Qualifier("parcelParser")
  private Parser<ParcelDescriptor> parcelParser;

  @Autowired
  @Qualifier("alternativesParser")
  private Parser<AlternativesDescriptor> alternativesParser;

  @Autowired
  @Qualifier("permissionsParser")
  private Parser<PermissionsDescriptor> permissionsParser;

  @Autowired
  @Qualifier("parcelRunner")
  private DescriptorRunner<ParcelDescriptor> parcelRunner;

  @Autowired
  @Qualifier("alternativesRunner")
  private DescriptorRunner<AlternativesDescriptor> alternativesRunner;

  @Autowired
  @Qualifier("permissionsRunner")
  private DescriptorRunner<PermissionsDescriptor> permissionsRunner;

  @Override
  public boolean run(String target, Writer writer) throws IOException {
    File parcelFile = new File(target);
    writer.write(String.format("Validating: %s\n", parcelFile.getPath()));

    if (!checkExistence(parcelFile, false, writer)) {
      return false;
    }

    String expectedDir;
    String distro;
    Matcher parcelMatcher = PARCEL_PATTERN.matcher(parcelFile.getName());
    if (parcelMatcher.find()) {
      expectedDir = parcelMatcher.group(1) + '-' + parcelMatcher.group(2);
      distro = parcelMatcher.group(3);
    } else {
      writer.write(String.format("==> %s is not a valid parcel filename\n",
                                 parcelFile.getName()));
      return false;
    }

    if (!KNOWN_DISTROS.contains(distro)) {
      writer.write(String.format("==> %s does not appear to be a distro supported by CM\n",
                                 distro));
    }

    FileInputStream fin = null;
    BufferedInputStream bin = null;
    GzipCompressorInputStream gin = null;
    TarArchiveInputStream tin= null;
    try {
      InputStream in = null;

      fin = new FileInputStream(parcelFile);
      bin = new BufferedInputStream(fin);
      try {
        gin = new GzipCompressorInputStream(bin);
        in = gin;
      } catch (IOException e) {
        // It's not compressed. Proceed as if uncompressed tar.
        writer.write(String.format("==> Warning: Parcel is not compressed with gzip\n"));
        in = bin;
      }
      tin = new TarArchiveInputStream(in);

      byte[] parcelJson = null;
      byte[] alternativesJson = null;
      byte[] permissionsJson = null;

      Map<String, Boolean> tarEntries = Maps.newHashMap();
      Set<String> unexpectedDirs = Sets.newHashSet();
      for (TarArchiveEntry e = tin.getNextTarEntry(); e != null; e = tin.getNextTarEntry()) {
        String name = e.getName();

        // Remove trailing '/'
        tarEntries.put(name.replaceAll("/$", ""), e.isDirectory());

        if (!StringUtils.startsWith(name, expectedDir)) {
          unexpectedDirs.add(name.split("/")[0]);
        }

        if (e.getName().equals(expectedDir + PARCEL_JSON_PATH)) {
          parcelJson = new byte[(int) e.getSize()];
          tin.read(parcelJson);
        } else if (e.getName().equals(expectedDir + ALTERNATIVES_JSON_PATH)) {
          alternativesJson = new byte[(int) e.getSize()];
          tin.read(alternativesJson);
        } else if (e.getName().equals(expectedDir + PERMISSIONS_JSON_PATH)) {
          permissionsJson = new byte[(int) e.getSize()];
          tin.read(permissionsJson);
        }
      }

      boolean ret = true;

      if (!unexpectedDirs.isEmpty()) {
        writer.write(String.format("==> The following unexpected top level directories were observed: %s\n",
                                   unexpectedDirs.toString()));
        writer.write(String.format("===> The only valid top level directory, based on parcel filename, is: %s\n",
                                   expectedDir));
        ret = false;
      }

      ret &= checkParcelJson(expectedDir, parcelJson, tarEntries, writer);
      ret &= checkAlternatives(expectedDir, alternativesJson, tarEntries, writer);
      ret &= checkPermissions(expectedDir, permissionsJson, tarEntries, writer);

      return ret;
    } catch (IOException e) {
      writer.write(String.format("==> %s: %s\n", e.getClass().getName(), e.getMessage()));
      return false;
    } finally {
      IOUtils.closeQuietly(tin);
      IOUtils.closeQuietly(gin);
      IOUtils.closeQuietly(bin);
      IOUtils.closeQuietly(fin);
    }
  }

  private boolean checkExistence(File file, boolean directory, Writer writer)
      throws IOException {
    if (!file.exists()) {
      writer.write(String.format("==> %s does not exist.\n",
                                 file.getPath()));
      return false;
    } else if (directory && !file.isDirectory()) {
      writer.write(String.format("==> %s is not a directory.\n",
                                 file.getPath()));
      return false;
    } else if (!directory && !file.isFile()) {
      writer.write(String.format("==> %s is not a file.\n",
                                 file.getPath()));
      return false;
    }
    return true;
  }

  private boolean checkExistence(Map<String, Boolean> entries, String path, Boolean directory, Writer writer)
        throws IOException {
    Boolean isDirectory = entries.get(path);

    if (!entries.keySet().contains(path)) {
      writer.write(String.format("==> %s does not exist.\n", path));
      return false;
    } else if (directory && !isDirectory) {
      writer.write(String.format("==> %s is not a directory.\n", path));
      return false;
    } else if (!directory && isDirectory) {
      writer.write(String.format("==> %s is not a file.\n", path));
      return false;
    }
    return true;
  }

  private boolean checkParcelJson(String dirName, byte[] data,
                                  Map<String, Boolean> entries, Writer writer)
      throws IOException {
    String jsonPath = new File(dirName, PARCEL_JSON_PATH).getPath();

    if (data == null) {
      writer.write(String.format("==> No parcel.json file found in required location: %s\n",
                                 jsonPath));
      return false;
    }

    if (!parcelRunner.run(jsonPath, data, writer)) {
      return false;
    }

    boolean ret = true;
    ParcelDescriptor parcel = parcelParser.parse(data);
    String expectedDirName = String.format("%s-%s", parcel.getName(),
        parcel.getVersion());
    if (!expectedDirName.equals(dirName)) {
      writer.write(String.format(
          "==> Parcel directory '%s' must be named '%s' to match parcel.json file\n",
          dirName, expectedDirName));
      ret = false;
    }

    String envScript = parcel.getScripts().getDefines();
    if (envScript != null) {
      String envPath = new File(dirName + "/meta", envScript).getPath();
      if (!checkExistence(entries, envPath, false, writer)) {
        ret = false;
      }
    }

    return ret;
  }

  private boolean checkAlternatives(String dirName, byte[] data,
                                    Map<String, Boolean> entries, Writer writer)
      throws IOException {
    if (data == null) {
      return true;
    }

    String jsonPath = new File(dirName, ALTERNATIVES_JSON_PATH).getPath();

    if (!alternativesRunner.run(jsonPath, data, writer)) {
      return false;
    }

    boolean ret = true;
    AlternativesDescriptor alternatives = alternativesParser.parse(data);
    for (Map.Entry<String, AlternativeDescriptor> e : alternatives.getAlternatives().entrySet()) {
      String source = new File(dirName, e.getValue().getSource()).getPath();
      if (!checkExistence(entries, source, e.getValue().getIsDirectory(), writer)) {
        ret = false;
      }
    }

    return ret;
  }

  private boolean checkPermissions(String dirName, byte[] data,
                                   Map<String, Boolean> entries, Writer writer)
      throws IOException {
    if (data == null) {
      return true;
    }

    String jsonPath = new File(dirName, PERMISSIONS_JSON_PATH).getPath();

    if (!permissionsRunner.run(jsonPath, data, writer)) {
      return false;
    }

    boolean ret = true;
    PermissionsDescriptor permissions = permissionsParser.parse(data);
    for (Map.Entry<String, PermissionDescriptor> e : permissions.getPermissions().entrySet()) {
      String file = new File(dirName, e.getKey()).getPath();
      if (!checkExistence(entries, file, entries.get(file), writer)) {
        ret = false;
      }
    }

    return ret;
  }
}
