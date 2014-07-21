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

import static org.junit.Assert.*;

import com.cloudera.csd.descriptors.CompatibilityDescriptor;
import com.cloudera.csd.descriptors.CompatibilityDescriptor.VersionRange;
import com.cloudera.csd.descriptors.ConfigWriter;
import com.cloudera.csd.descriptors.GatewayDescriptor;
import com.cloudera.csd.descriptors.GracefulStopDescriptor;
import com.cloudera.csd.descriptors.RoleCommandDescriptor;
import com.cloudera.csd.descriptors.RoleDescriptor;
import com.cloudera.csd.descriptors.RoleExternalLink;
import com.cloudera.csd.descriptors.ServiceCommandDescriptor;
import com.cloudera.csd.descriptors.ServiceCommandDescriptor.RunMode;
import com.cloudera.csd.descriptors.ServiceDependency;
import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.descriptors.ServiceInitDescriptor;
import com.cloudera.csd.descriptors.TopologyDescriptor;
import com.cloudera.csd.descriptors.generators.AuxConfigGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.HadoopXMLGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.PropertiesGenerator;
import com.cloudera.csd.descriptors.generators.PeerConfigGenerator;
import com.cloudera.csd.descriptors.parameters.BooleanParameter;
import com.cloudera.csd.descriptors.parameters.CsdParamUnits;
import com.cloudera.csd.descriptors.parameters.DoubleParameter;
import com.cloudera.csd.descriptors.parameters.LongParameter;
import com.cloudera.csd.descriptors.parameters.MemoryParameter;
import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.descriptors.parameters.PasswordParameter;
import com.cloudera.csd.descriptors.parameters.PathArrayParameter;
import com.cloudera.csd.descriptors.parameters.PathParameter;
import com.cloudera.csd.descriptors.parameters.PortNumberParameter;
import com.cloudera.csd.descriptors.parameters.StringArrayParameter;
import com.cloudera.csd.descriptors.parameters.StringEnumParameter;
import com.cloudera.csd.descriptors.parameters.StringParameter;
import com.cloudera.csd.descriptors.parameters.URIArrayParameter;
import com.cloudera.csd.descriptors.parameters.URIParameter;
import com.cloudera.csd.validation.SdlTestUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class JsonSdlParserTest {

  private JsonSdlParser parser = new JsonSdlParser();

  @Test
  public void testParseFullFile() throws Exception {
    ServiceDescriptor descriptor = parser.parse(getSdl("service_full.sdl"));
    assertNotNull(descriptor);
    assertEquals(descriptor.getName(), "ECHO");
    assertEquals(Integer.valueOf(1), descriptor.getMaxInstances());

    assertNotNull(descriptor.getParcel());
    assertEquals("http://mywebsite.com", descriptor.getParcel().getRepoUrl());
    assertEquals(ImmutableSet.of("req"), descriptor.getParcel().getRequiredTags());
    assertEquals(ImmutableSet.of("opt"), descriptor.getParcel().getOptionalTags());
    assertTrue(descriptor.isInExpressWizard());

    assertEquals("ECHO_MASTER_SERVER", Iterables.getOnlyElement(descriptor.getRolesWithExternalLinks()));

    GatewayDescriptor clientCfg = descriptor.getGateway();
    assertNotNull(clientCfg);
    assertEquals("echo-conf", clientCfg.getAlternatives().getName());
    assertEquals(50, clientCfg.getAlternatives().getPriority());
    assertEquals("/etc/echo", clientCfg.getAlternatives().getLinkRoot());
    assertEquals(2, clientCfg.getParameters().size());
    assertEquals(2, clientCfg.getConfigWriter().getGenerators().size());

    assertEquals(2, descriptor.getHdfsDirs().size());
    
    ServiceInitDescriptor initRunner= descriptor.getServiceInit();
    assertNotNull(initRunner);
    assertEquals(1, initRunner.getPreStartSteps().size());
    assertFalse(Iterables.getOnlyElement(initRunner.getPreStartSteps()).isFailureAllowed());
    assertEquals(1, initRunner.getPostStartSteps().size());
    assertTrue(Iterables.getOnlyElement(initRunner.getPostStartSteps()).isFailureAllowed());
    
    assertEquals(2, descriptor.getRoles().size());
    Map<String, RoleDescriptor> name2role = Maps.newHashMap();
    for (RoleDescriptor desc : descriptor.getRoles()) {
      name2role.put(desc.getName(), desc);
    }

    RoleDescriptor master = name2role.get("ECHO_MASTER_SERVER");
    assertNotNull(master);
    assertNotNull(master.getLogging());
    assertEquals("master.log.dir", master.getLogging().getConfigName());
    assertEquals("Master Servers", master.getPluralLabel());

    RoleExternalLink externalLink = master.getExternalLink();
    assertNotNull(externalLink);
    assertEquals("master_web_ui", externalLink.getName());
    assertEquals("Master WebUI", externalLink.getLabel());
    assertEquals("http://myhost.com:80", externalLink.getUrl());
    List<RoleExternalLink> moreLinks = master.getAdditionalExternalLinks();
    assertNotNull(moreLinks);
    assertEquals(1, moreLinks.size());

    TopologyDescriptor topology = master.getTopology();
    assertNotNull(topology);
    assertEquals(Integer.valueOf(1), topology.getMinInstances());
    assertEquals(Integer.valueOf(1), topology.getMaxInstances());
    
    RoleDescriptor role = name2role.get("ECHO_WEBSERVER");
    assertNotNull(role);
    assertNotNull(role.getLogging());
    assertNull(role.getLogging().getConfigName());
    assertEquals("Web Servers", role.getPluralLabel());

    assertNotNull(descriptor.getCommands());
    assertEquals(2, descriptor.getCommands().size());
    int found = 0;
    for (ServiceCommandDescriptor cmd : descriptor.getCommands()) {
      assertEquals("role_cmd1", cmd.getRoleCommand());
      if (cmd.getName().equals("service_cmd1")) {
        assertEquals(RunMode.ALL, cmd.getRunMode());
        found++;
      } else if (cmd.getName().equals("service_cmd2")) {
        assertEquals(RunMode.SINGLE, cmd.getRunMode());
        found++;
      } else {
        fail();
      }
    }
    assertEquals(2, found);

    assertNotNull(descriptor.getStopRunner());
    GracefulStopDescriptor stopDesc = descriptor.getStopRunner();
    assertEquals(180000, stopDesc.getTimeout());
    assertEquals("ECHO_MASTER_SERVER", stopDesc.getMasterRole());
    assertEquals("scripts/graceful_stop.sh", stopDesc.getRunner().getProgram());
    assertEquals(ImmutableList.of("ECHO_WEBSERVER"), stopDesc.getRelevantRoleTypes());
  }

  @Test
  public void testCompatibilty() throws Exception {
    ServiceDescriptor descriptor = parser.parse(getSdl("service_full.sdl"));
    assertNotNull(descriptor);

    CompatibilityDescriptor desc = descriptor.getCompatibility();
    assertNotNull(desc);
    VersionRange cdhRange = desc.getCdhVersion();
    assertNotNull(cdhRange);
    assertEquals("4", cdhRange.getMin());
    assertEquals("5", cdhRange.getMax());

    // compatibility
    Long compatibility = desc.getGeneration();
    assertNotNull(compatibility);
    assertEquals(Long.valueOf(2l), compatibility);
  }

  @Test
  public void testParseUnknownElement() throws Exception {
    ServiceDescriptor descriptor = parser
        .parse(getSdl("service_unknown_elements.sdl"));
    assertEquals(descriptor.getName(), "ECHO");
  }

  @Test(expected = IOException.class)
  public void testBadJson() throws Exception {
    parser.parse(getSdl("service_badjson.sdl"));
  }

  @Test
  public void testParametersParsing() throws Exception {
    ServiceDescriptor descriptor = parser.parse(getSdl("service_full.sdl"));
    assertEquals(3, descriptor.getParameters().size());
    int found = 0;
    for (Parameter<?> p : descriptor.getParameters()) {
      // check that parameters are parsed polymorphically
      if (p.getName().equals("service_var1")) {
        assertTrue(p instanceof StringParameter);
        assertTrue(p.isConfigurableInWizard());
        found++;
      } else if (p.getName().equals("service_var2")) {
        assertTrue(p instanceof LongParameter);
        LongParameter lp = (LongParameter)p;
        assertEquals(1, lp.getMin().longValue());
        assertNull(lp.getMax());
        assertEquals(CsdParamUnits.MEGABYTES, lp.getUnit());
        found++;
      } else if (p.getName().equals("service_var3")) {
        assertTrue(p instanceof LongParameter);
        LongParameter lp = (LongParameter)p;
        assertEquals(1, lp.getMin().longValue());
        assertEquals(2, lp.getSoftMin().longValue());
        assertEquals(3, lp.getSoftMax().longValue());
        assertEquals(4, lp.getMax().longValue());
        assertNull(lp.getUnit());
        found++;
      }
    }
    assertEquals(3, found);

    // Check service dependencies
    assertEquals(2, descriptor.getServiceDependencies().size());
    found = 0;
    for (ServiceDependency sd : descriptor.getServiceDependencies()) {
      if (sd.getName().equals("ZOOKEEPER")) {
        assertFalse(sd.isRequired());
        found++;
      } else if (sd.getName().equals("HDFS")) {
        assertTrue(sd.isRequired());
        found++;
      }
    }
    assertEquals(2, found);

    found = 0;

    List<RoleDescriptor> roles = descriptor.getRoles();
    assertEquals(2, roles.size());
    Map<String, RoleDescriptor> rds = SdlTestUtils.makeRoleMap(roles);
    RoleDescriptor rd = rds.get("ECHO_WEBSERVER");
    // check role command
    assertEquals(1, rd.getCommands().size());
    RoleCommandDescriptor rcd = Iterables.getOnlyElement(rd.getCommands());
    
    assertEquals(14, rd.getParameters().size());
    for (Parameter<?> p : rd.getParameters()) {
      // check that parameters are parsed polymorphically
      if (p.getName().equals("role_var1")) {
        assertTrue(p instanceof StringParameter);
        StringParameter sp = (StringParameter)p;
        assertEquals("role_var1_default", sp.getDefault());
        found++;
      } else if (p.getName().equals("role_var2")) {
        assertTrue(p instanceof LongParameter);
        LongParameter lp = (LongParameter)p;
        assertNull(lp.getMin());
        assertNull(lp.getMax());
        assertEquals(CsdParamUnits.SECONDS, lp.getUnit());
        found++;
      } else if (p.getName().equals("role_var3")) {
        assertTrue(p instanceof BooleanParameter);
        BooleanParameter bp = (BooleanParameter)p;
        assertTrue(bp.getDefault());
        found++;
      } else if (p.getName().equals("role_var4")) {
        assertTrue(p instanceof DoubleParameter);
        DoubleParameter dp = (DoubleParameter)p;
        assertNotNull(dp.getMin());
        assertNotNull(dp.getMax());
        assertEquals(CsdParamUnits.TIMES, dp.getUnit());
        found++;
      } else if (p.getName().equals("role_var5")) {
        assertTrue(p instanceof PathArrayParameter);
        PathArrayParameter dp = (PathArrayParameter)p;
        assertNotNull(dp.getMinLength());
        assertNotNull(dp.getMaxLength());
        assertNotNull(dp.getPathType());
        found++;
      } else if (p.getName().equals("role_var6")) {
        assertTrue(p instanceof StringArrayParameter);
        StringArrayParameter dp = (StringArrayParameter)p;
        ImmutableList expected = ImmutableList.of("foo", "bar");
        assertEquals(expected, dp.getDefault());
        assertNull(dp.getMinLength());
        assertNotNull(dp.getMaxLength());
        assertNotNull(dp.getSeparator());
        found++;
      } else if (p.getName().equals("role_var7")) {
        assertTrue(p instanceof StringEnumParameter);
        StringEnumParameter dp = (StringEnumParameter)p;
        assertEquals(2, dp.getValidValues().size());
        assertNotNull(dp.getDefault());
        found++;
      } else if (p.getName().equals("role_var8")) {
        assertTrue(p instanceof URIArrayParameter);
        URIArrayParameter dp = (URIArrayParameter)p;
        ImmutableList expected = ImmutableList.of("ldap://foo", "ldaps://bar");
        assertEquals(expected, dp.getDefault());
        assertNotNull(dp.getMinLength());
        assertNotNull(dp.getMaxLength());
        assertEquals(2, dp.getAllowedSchemes().size());
        assertFalse(dp.isOpaque());
        found++;
      } else if (p.getName().equals("role_var9")) {
        assertTrue(p instanceof URIParameter);
        URIParameter dp = (URIParameter)p;
        assertEquals(2, dp.getAllowedSchemes().size());
        assertTrue(dp.isOpaque());
        found++;
      } else if (p.getName().equals("role_var10")) {
        assertTrue(p instanceof PathParameter);
        PathParameter dp = (PathParameter)p;
        assertNotNull(dp.getPathType());
        found++;
      } else if (p.getName().equals("role_var11")) {
        assertTrue(p instanceof PortNumberParameter);
        PortNumberParameter dp = (PortNumberParameter) p;
        assertTrue(dp.isZeroAllowed());
        assertTrue(dp.isNegativeOneAllowed());
        assertTrue(dp.isOutbound());
        found++;
      } else if (p.getName().equals("role_var12")) {
        assertTrue(p instanceof StringParameter);
        StringParameter dp = (StringParameter) p;
        assertTrue(dp.isSensitive());
        found++;
      } else if (p.getName().equals("role_var13")) {
        assertTrue(p instanceof PasswordParameter);
        PasswordParameter dp = (PasswordParameter) p;
        found++;
      } else if (p.getName().equals("echo_server_heap")) {
        assertTrue(p instanceof MemoryParameter);
        MemoryParameter mp = (MemoryParameter)p;
        assertEquals(1024 * 1024 * 1024, mp.getDefault().longValue());
        assertEquals(1.3, mp.getScaleFactor().doubleValue(), 0);
        assertEquals(100, mp.getAutoConfigShare().intValue());
        found++;
      }
    }
    assertEquals(14, found);
    
    // Check that config files are parsed correctly
    ConfigWriter cw = rd.getConfigWriter();
    assertEquals(3, cw.getGenerators().size());
    found = 0;
    for (ConfigGenerator gen : cw.getGenerators()) {
      if (gen.getFilename().equals("sample_xml_file.xml")) {
        assertTrue(gen instanceof HadoopXMLGenerator);
        assertNull(gen.getIncludedParams());
        assertEquals(2, gen.getExcludedParams().size());
        found++;
      } else if (gen.getFilename().equals("sample_props_file.properties")) {
        assertTrue(gen instanceof PropertiesGenerator);
        assertEquals(2, gen.getIncludedParams().size());
        assertNull(gen.getExcludedParams());
        found++;
      } else if (gen.getFilename().equals("sample_role_props_file.properties")) {
        assertTrue(gen instanceof PropertiesGenerator);
        assertEquals(2, gen.getIncludedParams().size());
        assertNull(gen.getExcludedParams());
        found++;
      }
    }
    assertEquals(3, found);
    found = 0;
    assertEquals(2, cw.getPeerConfigGenerators().size());
    for (PeerConfigGenerator gen : cw.getPeerConfigGenerators()) {
      if (gen.getFilename().equals("sample_role_peer_file.properties")) {
        assertEquals(2, gen.getParams().size());
        found++;
      } else if (gen.getFilename().equals("sample_master_peer_file.properties")) {
        assertEquals(1, gen.getParams().size());
        assertEquals("ECHO_MASTER_SERVER", gen.getRoleName());
        found++;
      }
    }
    assertEquals(2, found);
    found = 0;
    assertEquals(1, cw.getAuxConfigGenerators().size());
    for (AuxConfigGenerator gen : cw.getAuxConfigGenerators()) {
      if (gen.getFilename().equals("some_aux_file.json")) {
        assertEquals("aux/filename.json", gen.getSourceFilename());
        found++;
      }
    }
    assertEquals(1, found);
  }

  private byte[] getSdl(String name) throws IOException {

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
