// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.AlternativeDescriptor;
import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class JsonAlternativesParserTest {

  private JsonAlternativesParser parser = new JsonAlternativesParser();

  @Test
  public void testParseGoodFile() throws IOException {
    AlternativesDescriptor descriptor = parser.parse(ParcelTestUtils.getParcelJson("good_alternatives.json"));
    Map<String, AlternativeDescriptor> alternatives = descriptor.getAlternatives();
    assertEquals(2, alternatives.size());

    AlternativeDescriptor beeline = alternatives.get("beeline");
    assertNotNull(beeline);
    assertEquals("/usr/bin/beeline", beeline.getDestination());
    assertEquals("bin/beeline", beeline.getSource());
    assertEquals(10, beeline.getPriority().intValue());
    assertEquals(false, beeline.getIsDirectory());

    AlternativeDescriptor hiveConf = alternatives.get("hive-conf");
    assertNotNull(hiveConf);
    assertEquals("/etc/hive/conf", hiveConf.getDestination());
    assertEquals("etc/hive/conf.dist", hiveConf.getSource());
    assertEquals(20, hiveConf.getPriority().intValue());
    assertEquals(true, hiveConf.getIsDirectory());
  }

  @Test
  public void testParseEmptyFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("empty.json"));
  }
}
