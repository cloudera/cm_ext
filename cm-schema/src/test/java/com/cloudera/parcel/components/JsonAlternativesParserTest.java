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
