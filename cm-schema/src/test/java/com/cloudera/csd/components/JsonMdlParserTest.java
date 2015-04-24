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
package com.cloudera.csd.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.SdlTestUtils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class JsonMdlParserTest {

  private JsonMdlParser parser = new JsonMdlParser();

  @Test
  public void testParseFullFile() throws Exception {
    ServiceMonitoringDefinitionsDescriptor descriptor =
        parser.parse(getMdl("service_full.mdl"));
    assertNotNull(descriptor);
    assertEquals(descriptor.getName(), "ECHO");
    assertEquals(2, descriptor.getMetricEntityAttributeDefinitions().size());
    assertEquals(2, descriptor.getMetricEntityTypeDefinitions().size());
    assertEquals(3, descriptor.getMetricDefinitions().size());
    assertEquals(2, descriptor.getRoles().size());
    for (RoleMonitoringDefinitionsDescriptor role : descriptor.getRoles()) {
      assertEquals(3, role.getMetricDefinitions().size());
    }
  }

  @Test
  public void testParseUnknownElement() throws Exception {
    ServiceMonitoringDefinitionsDescriptor descriptor = parser
        .parse(getMdl("service_unknown_elements.mdl"));
    assertEquals(descriptor.getName(), "ECHO");
  }

  @Test(expected = IOException.class)
  public void testBadJson() throws Exception {
    parser.parse(getMdl("service_badjson.sdl"));
  }

  private byte[] getMdl(String name) throws IOException {
    InputStream stream = null;
    try {
      stream = JsonSdlParserTest.class
          .getResourceAsStream(SdlTestUtils.SDL_PARSER_RESOURCE_PATH + name);
      return IOUtils.toByteArray(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
}
