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

import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.cloudera.parcel.descriptors.ParcelInfoDescriptor;
import com.cloudera.parcel.descriptors.VersionServicesRestartDescriptor;
import com.cloudera.parcel.descriptors.VersionServicesRestartDescriptor.Scope;
import com.cloudera.parcel.validation.ParcelTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.junit.Test;

public class JsonManifestParserTest {

  private JsonManifestParser parser = new JsonManifestParser();

  @Test
  public void testParseGoodFile() throws IOException {
    ManifestDescriptor descriptor = parser.parse(ParcelTestUtils.getParcelJson("good_manifest.json"));
    assertEquals(new Instant(1392073012), descriptor.getLastUpdated());

    List<ParcelInfoDescriptor> parcels = descriptor.getParcels();
    assertEquals(3, parcels.size());

    ParcelInfoDescriptor parcelInfo = parcels.get(0);
    assertEquals("CDH-5.0.0-0.cdh5b2.p0.282-wheezy.parcel", parcelInfo.getParcelName());
    assertEquals("ec6e65de6e192949fd095b391b9e3b8b2d13e780", parcelInfo.getHash());
    assertEquals("IMPALA, SOLR, SPARK", parcelInfo.getReplaces());
    assertEquals(27, parcelInfo.getComponents().size());

    parcelInfo = parcels.get(1);
    assertEquals("CDH-5.0.0-0.cdh5b2.p0.30-el6.parcel", parcelInfo.getParcelName());
    assertEquals("d4d5d146e00c2d3ff19e95476f3485be64fd0f71", parcelInfo.getHash());
    assertEquals("IMPALA, SOLR, SPARK", parcelInfo.getReplaces());
    assertEquals(27, parcelInfo.getComponents().size());

    parcelInfo = parcels.get(2);
    assertEquals("CDH-5.5.0-0.cdh5b2.p0.1-el6.parcel", parcelInfo.getParcelName());
    assertEquals("f4asdas146e00c2d3ff19e95476f3485be64fd0f", parcelInfo.getHash());
    assertEquals("IMPALA, SOLR, SPARK", parcelInfo.getReplaces());
    assertEquals(27, parcelInfo.getComponents().size());

    assertNotNull(parcelInfo.getServicesRestartInfo());
    assertNotNull(parcelInfo.getServicesRestartInfo().getVersionInfo());

    Map<String, VersionServicesRestartDescriptor> versionServicesRestartDescriptorMap =
      parcelInfo.getServicesRestartInfo().getVersionInfo();
    assertEquals(3, versionServicesRestartDescriptorMap.size());
    assertTrue(versionServicesRestartDescriptorMap.containsKey("5.5.0-0.cdh5b2.p0.1"));
    assertTrue(versionServicesRestartDescriptorMap.containsKey("5.5.0-0.cdh5b2"));
    assertTrue(versionServicesRestartDescriptorMap.containsKey("5.5.0-0.cdh5b"));


    VersionServicesRestartDescriptor p01 =
      versionServicesRestartDescriptorMap.get("5.5.0-0.cdh5b2.p0.1");
    VersionServicesRestartDescriptor p00 =
      versionServicesRestartDescriptorMap.get("5.5.0-0.cdh5b2");
    VersionServicesRestartDescriptor base =
      versionServicesRestartDescriptorMap.get("5.5.0-0.cdh5b");

    assertNull(p01.getChildVersion());
    assertEquals("5.5.0-0.cdh5b2", p01.getParentVersion());
    assertTrue(p01.getServiceInfo().containsKey("IMPALA"));
    assertTrue(p01.getServiceInfo().containsValue(Scope.DEPENDENTS_ONLY));

    assertEquals("5.5.0-0.cdh5b2.p0.1", p00.getChildVersion());
    assertEquals("5.5.0-0.cdh5b", p00.getParentVersion());
    assertTrue(p00.getServiceInfo().containsKey("HDFS"));
    assertTrue(p00.getServiceInfo().containsValue(Scope.SERVICE_ONLY));

    assertNull(base.getParentVersion());
    assertEquals("5.5.0-0.cdh5b2", base.getChildVersion());
    assertTrue(base.getServiceInfo().containsKey("HBASE"));
    assertTrue(base.getServiceInfo().containsValue(Scope.SERVICE_AND_DEPENDENTS));
  }

  @Test
  public void testParseEmptyFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("empty.json"));
  }
}
