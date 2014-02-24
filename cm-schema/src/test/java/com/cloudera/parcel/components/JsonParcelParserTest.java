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
package com.cloudera.parcel.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.ComponentDescriptor;
import com.cloudera.parcel.descriptors.PackageDescriptor;
import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.parcel.descriptors.UserDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class JsonParcelParserTest {

  private JsonParcelParser parser = new JsonParcelParser();

  @Test
  public void testParseGoodFile() throws IOException {
    ParcelDescriptor parcel = parser.parse(ParcelTestUtils.getParcelJson("good_parcel.json"));
    assertNotNull(parcel);
    assertEquals("CDH", parcel.getName());
    assertEquals("5.0.0", parcel.getVersion());

    Map<String, String> extraVersionInfo = parcel.getExtraVersionInfo();
    assertEquals(3, extraVersionInfo.size());
    assertEquals("5.0.0-0.cdh5b2.p0.282-wheezy", extraVersionInfo.get("fullVersion"));
    assertEquals("cdh5.0.0", extraVersionInfo.get("baseVersion"));
    assertEquals("0", extraVersionInfo.get("patchCount"));

    assertEquals("foo >= 1.0", parcel.getDepends());
    assertEquals("bar", parcel.getReplaces());
    assertEquals("baz < 3.0", parcel.getConflicts());

    assertEquals(true, parcel.getSetActiveSymlink());

    assertEquals(ImmutableSet.of("cdh", "impala", "solr"),
                 parcel.getProvides());

    assertEquals("cdh_env.sh", parcel.getScripts().getDefines());

    Set<PackageDescriptor> packages = parcel.getPackages();
    assertEquals(3, packages.size());

    Set<String> names = Sets.newHashSet();
    for (PackageDescriptor pkg : packages) {
      assertEquals("2.2.0+cdh5.0.0+609-0.cdh5b2.p0.386~precise-cdh5.0.0", pkg.getVersion());
      names.add(pkg.getName());
    }
    assertEquals(ImmutableSet.of("hadoop", "hadoop-client", "hadoop-hdfs"), names);

    Set<ComponentDescriptor> components = parcel.getComponents();
    assertEquals(2, components.size());
    names = Sets.newHashSet();
    for (ComponentDescriptor cmp : components) {
      assertEquals("2.2.0-cdh5.0.0-SNAPSHOT", cmp.getVersion());
      assertEquals("2.2.0+cdh5.0.0+609", cmp.getPkg_version());
      names.add(cmp.getName());
    }
    assertEquals(ImmutableSet.of("hadoop", "hadoop-hdfs"), names);

    Map<String, UserDescriptor> users = parcel.getUsers();
    assertEquals(2, users.size());

    UserDescriptor hdfs = users.get("hdfs");
    assertNotNull(hdfs);
    assertEquals("Hadoop HDFS", hdfs.getLongname());
    assertEquals("/var/lib/hadoop-hdfs", hdfs.getHome());
    assertEquals("/bin/bash", hdfs.getShell());
    assertEquals(ImmutableSet.of("hadoop"), hdfs.getExtra_groups());

    UserDescriptor impala = users.get("impala");
    assertEquals("Impala", impala.getLongname());
    assertEquals("/var/run/impala", impala.getHome());
    assertEquals("/bin/bash", impala.getShell());
    assertEquals(ImmutableSet.of("hive", "hdfs"), impala.getExtra_groups());

    assertEquals(ImmutableSet.of("hadoop"), parcel.getGroups());
  }

  @Test
  public void testParseMinimalFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("minimal_parcel.json"));
  }

  @Test(expected=UnrecognizedPropertyException.class)
  public void testParseBadScriptsFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("bad_scripts.json"));
  }

  @Test(expected=UnrecognizedPropertyException.class)
  public void testParseUnkownPropertyScriptsFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("unknown_props_parcel.json"));
  }

  @Test
  public void testParseEmptyFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("empty.json"));
  }
}
