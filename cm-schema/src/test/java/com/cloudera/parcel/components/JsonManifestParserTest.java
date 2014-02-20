// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.cloudera.parcel.descriptors.ParcelInfoDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;

import java.io.IOException;
import java.util.List;

import org.joda.time.Instant;
import org.junit.Test;

public class JsonManifestParserTest {

  private JsonManifestParser parser = new JsonManifestParser();

  @Test
  public void testParseGoodFile() throws IOException {
    ManifestDescriptor descriptor = parser.parse(ParcelTestUtils.getParcelJson("good_manifest.json"));
    assertEquals(new Instant(1392073012), descriptor.getLastUpdated());

    List<ParcelInfoDescriptor> parcels = descriptor.getParcels();
    assertEquals(2, parcels.size());

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
  }

  @Test
  public void testParseEmptyFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("empty.json"));
  }
}
