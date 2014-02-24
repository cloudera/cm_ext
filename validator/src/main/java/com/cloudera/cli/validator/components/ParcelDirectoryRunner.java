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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This {@link #ValidationRunner} validates a complete parcel directory.
 *
 * It runs individual file validators on parcel json files, and also validates
 * the relationship between the contexts of the directory and the json files.
 */
@Component
public class ParcelDirectoryRunner implements ValidationRunner {

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
  private ValidationRunner parcelRunner;

  @Autowired
  @Qualifier("alternativesRunner")
  private ValidationRunner alternativesRunner;

  @Autowired
  @Qualifier("permissionsRunner")
  private ValidationRunner permissionsRunner;

  @Override
  public boolean run(String target, Writer writer) throws IOException {
    File parcelDir = new File(target);
    writer.write(String.format("Validating: %s\n",
        parcelDir.getPath()));

    if (!checkExistence(parcelDir, true, writer)) {
      return false;
    }

    File metaDir = new File(parcelDir, "meta");
    if (!checkExistence(metaDir, true, writer)) {
      return false;
    }

    boolean ret = true;

    ret &= checkParcelJson(parcelDir, metaDir, writer);

    ret &= checkAlternatives(parcelDir, metaDir, writer);

    ret &= checkPermissions(parcelDir, metaDir, writer);

    return ret;
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

  private boolean checkParcelJson(File parcelDir, File metaDir, Writer writer)
      throws IOException {
    File parcelJson = new File(metaDir, "parcel.json");
    if (!checkExistence(parcelJson, false, writer)) {
      return false;
    }

    if (!parcelRunner.run(parcelJson.getPath(), writer)) {
      return false;
    }

    boolean ret = true;
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(parcelJson);
      ParcelDescriptor parcel = parcelParser.parse(IOUtils.toByteArray(stream));
      String expectedDirName = String.format("%s-%s", parcel.getName(),
          parcel.getVersion());
      String actualDirName = parcelDir.getName();
      if (!expectedDirName.equals(actualDirName)) {
        writer.write(String.format(
            "==> Parcel directory '%s' must be named '%s' to match parcel.json file\n",
            actualDirName, expectedDirName));
        ret = false;
      }

      String envScript = parcel.getScripts().getDefines();
      if (envScript != null) {
        File envFile = new File(metaDir, envScript);
        if (!checkExistence(envFile, false, writer)) {
          ret = false;
        }
      }

    } finally {
      IOUtils.closeQuietly(stream);
    }

    return ret;
  }

  private boolean checkAlternatives(File parcelDir, File metaDir, Writer writer)
      throws IOException {
    File alternativesJson = new File(metaDir, "alternatives.json");
    if (!alternativesJson.exists()) {
      return true;
    }

    if (!checkExistence(alternativesJson, false, writer)) {
      return false;
    }

    if (!alternativesRunner.run(alternativesJson.getPath(), writer)) {
      return false;
    }

    boolean ret = true;
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(alternativesJson);
      AlternativesDescriptor alternatives = alternativesParser.parse(IOUtils.toByteArray(stream));
      for (Map.Entry<String, AlternativeDescriptor> e : alternatives.getAlternatives().entrySet()) {
        File source = new File(parcelDir, e.getValue().getSource());
        if (!checkExistence(source, e.getValue().getIsDirectory(), writer)) {
          ret = false;
        }
      }
    } finally {
      IOUtils.closeQuietly(stream);
    }

    return ret;
  }

  private boolean checkPermissions(File parcelDir, File metaDir, Writer writer)
      throws IOException {
    File permissionsJson = new File(metaDir, "permissions.json");
    if (!permissionsJson.exists()) {
      return true;
    }

    if (!checkExistence(permissionsJson, false, writer)) {
      return false;
    }

    if (!permissionsRunner.run(permissionsJson.getPath(), writer)) {
      return false;
    }

    boolean ret = true;
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(permissionsJson);
      PermissionsDescriptor permissions = permissionsParser.parse(IOUtils.toByteArray(stream));
      for (Map.Entry<String, PermissionDescriptor> e : permissions.getPermissions().entrySet()) {
        File file = new File(parcelDir, e.getKey());
        if (!checkExistence(file, file.isDirectory(), writer)) {
          ret = false;
        }
      }
    } finally {
      IOUtils.closeQuietly(stream);
    }

    return ret;
  }
}
