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
package com.cloudera.csd.tools;

import com.cloudera.csd.components.JsonMdlParser;
import com.cloudera.csd.components.JsonSdlObjectMapper;
import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.tools.MetricTools.MetricTool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetricTool that adds metrics from a "fixture" file of MetricDescriptors to
 * an input MDL.
 */
public class MetricDescriptorAddMetricsTool implements MetricTool {

  private static final Logger LOG = LoggerFactory.getLogger(
      MetricDescriptorAddMetricsTool.class);

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_INPUT_MDL = OptionBuilder
      .withLongOpt("mdl")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The base monitoring definitions file. This should " +
          "be partially populated with entity attribute and type information " +
          "as well as any manually defined metrics. This file should be in" +
          "ServiceMonitoringDefinitionsDescriptor format. See that class for " +
          "more information.")
      .isRequired(false)
      .create();

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_INPUT_FIXTURE = OptionBuilder
      .withLongOpt("fixture")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The fixture input file. This file should be a map " +
          "from service name, role name or metric entity type name to a " +
          "list of MetricDescriptors.")
      .isRequired(false)
      .create();

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_GENERATE_OUPTUT = OptionBuilder
      .withLongOpt("output")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The generated metric descriptor output file name. By " +
                       "default service_monitoring_definitions.json will be used.")
      .isRequired(false)
      .create();

  public static void addToolOptions(Options options) {
    options.addOption(OPT_INPUT_MDL);
    options.addOption(OPT_INPUT_FIXTURE);
    options.addOption(OPT_GENERATE_OUPTUT);
  }

  @Override
  public void run(CommandLine cmdLine, OutputStream out, OutputStream err)
      throws Exception {
    Preconditions.checkNotNull(cmdLine);
    Preconditions.checkNotNull(out);
    Preconditions.checkNotNull(err);

    FileInputStream mdlInputStream = null;
    FileInputStream metricInputStream = null;
    MapConfiguration config = generateAndValidateConfig(cmdLine);
    try {
      mdlInputStream =
          new FileInputStream(config.getString(OPT_INPUT_MDL.getLongOpt()));
      JsonSdlObjectMapper mapper = new JsonSdlObjectMapper();
      JsonMdlParser mdlParser = new JsonMdlParser(mapper);
      ServiceMonitoringDefinitionsDescriptor mdl =
          mdlParser.parse(IOUtils.toByteArray(mdlInputStream));
      ServiceMonitoringDefinitionsDescriptorImpl.Builder mdlBuilder =
          new ServiceMonitoringDefinitionsDescriptorImpl.Builder(mdl);

      metricInputStream =
          new FileInputStream(config.getString(OPT_INPUT_FIXTURE.getLongOpt()));
      Map<String, List<MetricDescriptor>> metrics =
          mapper.readValue(
              IOUtils.toByteArray(metricInputStream),
              new TypeReference<Map<String, List<MetricDescriptor>>>() { });

      if (metrics.containsKey(mdl.getName())) {
        mdlBuilder.addMetricDefinitions(metrics.get(mdl.getName()));
      }
      if (null != mdl.getRoles()) {
        List<RoleMonitoringDefinitionsDescriptor> roles = Lists.newArrayList();
        for (RoleMonitoringDefinitionsDescriptor role : mdl.getRoles()) {
          RoleMonitoringDefinitionsDescriptorImpl.Builder roleBuilder =
              new RoleMonitoringDefinitionsDescriptorImpl.Builder(role);
          if (metrics.containsKey(role.getName())) {
            roleBuilder.addMetricDefinitions(metrics.get(role.getName()));
          }
          roles.add(roleBuilder.build());
        }
        mdlBuilder.setRoles(roles);
      } 
      if (null != mdl.getMetricEntityTypeDefinitions()) {
        List<MetricEntityTypeDescriptor> entityTypes = Lists.newArrayList();
        for (MetricEntityTypeDescriptor entityType :
             mdl.getMetricEntityTypeDefinitions()) {
          MetricEntityTypeDescriptorImpl.Builder entityTypeBuilder =
              new MetricEntityTypeDescriptorImpl.Builder(entityType);
          if (metrics.containsKey(entityType.getName())) {
            entityTypeBuilder.addMetricDefinitions(
                metrics.get(entityType.getName()));
          }
          entityTypes.add(entityTypeBuilder.build());
        }
        mdlBuilder.setMetricEntityTypeDescriptors(entityTypes);
      }

      FileUtils.write(new File(config.getString(OPT_GENERATE_OUPTUT.getLongOpt())),
                      mdlParser.valueAsString(mdlBuilder.build(), true));
    } catch (Exception ex) {
      LOG.error("Could not run MetricGenerator tool.", ex);
      IOUtils.write(ex.getMessage() + "\n", err);
      throw ex;
    } finally {
      IOUtils.closeQuietly(mdlInputStream);
      IOUtils.closeQuietly(metricInputStream);
    }
  }

  private MapConfiguration generateAndValidateConfig(CommandLine cmdLine)
      throws ParseException {
    Preconditions.checkNotNull(cmdLine);
    MapConfiguration ret =
        new MapConfiguration(Maps.<String, Object>newHashMap());

    for (Option option : cmdLine.getOptions()) {
      ret.addProperty(option.getLongOpt(), option.getValue());
    }

    if (null == ret.getProperty(OPT_INPUT_MDL.getLongOpt()) ) {
      throw new ParseException(getName() + " missing MDL file argument");
    } else {
      String fileName = ret.getString(OPT_INPUT_MDL.getLongOpt());
      File file = new File(fileName);
      if (!file.exists()) {
        throw new ParseException("MDL file '" + fileName + "' does not " +
                                 "exist");
      } else if (!file.isFile()) {
         throw new ParseException("MDL file '" + fileName + "' is not a " +
                                  "file");
      }
    }

    if (null == ret.getProperty(OPT_INPUT_FIXTURE.getLongOpt()) ) {
      throw new ParseException(getName() + " missing fixture file argument");
    } else {
      String fileName = ret.getString(OPT_INPUT_FIXTURE.getLongOpt());
      File file = new File(fileName);
      if (!file.exists()) {
        throw new ParseException("Fixture file '" + fileName + "' does not " +
                                 "exist");
      } else if (!file.isFile()) {
         throw new ParseException("Fixture file '" + fileName + "' is not a " +
                                  "file");
      }
    }

    if (null == ret.getProperty(OPT_GENERATE_OUPTUT.getLongOpt()) ) {
      throw new ParseException(getName() + " missing output file argument");
    }

    return ret;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
