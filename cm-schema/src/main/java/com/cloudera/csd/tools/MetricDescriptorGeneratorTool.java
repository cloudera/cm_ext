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
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.tools.MetricTools.MetricTool;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

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

public class MetricDescriptorGeneratorTool implements MetricTool {

  private static final Logger LOG = LoggerFactory.getLogger(
      MetricDescriptorGeneratorTool.class);

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
      .withDescription("The fixture input file. This will be provided as " +
          "input to the adapter. The format of this file depends on the " +
          "adapter class used. See the adapter class for more information.")
      .isRequired(false)
      .create();

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_INPUT_CONVENTIONS = OptionBuilder
      .withLongOpt("conventions")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The conventions input file. This will be provided " +
          "as input to the adapter. The conventions file can be used to " +
          "supply domain specific metric context conventions to the " +
          "adapter. The format of this file depends on the adapter class " +
          "used. Se ethe adapter class for more information.")
      .isRequired(false)
      .create();

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_ADAPTER_CLASS = OptionBuilder
      .withLongOpt("adapter")
      .withArgName("FORMAT")
      .hasArg()
      .withDescription("The adapter class to use to parse the fixture and " +
          "produce the metric descriptors. Cloudera Manager " +
          "supplies the following adapters:\n" +
          "1) com.cloudera.csd.tools.codahale.CodahaleMetricAdapter")
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

  private static final String ADAPTER_CLASS_CONFIG = "adapterClass";
  private static final String DEFAULT_OUTPUT_FILE =
      "service_monitoring_definitions.json";

  public static void addToolOptions(Options options) {
    options.addOption(OPT_INPUT_MDL);
    options.addOption(OPT_INPUT_FIXTURE);
    options.addOption(OPT_INPUT_CONVENTIONS);
    options.addOption(OPT_ADAPTER_CLASS);
    options.addOption(OPT_GENERATE_OUPTUT);
  }

  @Override
  public void run(CommandLine cmdLine, OutputStream out, OutputStream err)
      throws Exception {
    Preconditions.checkNotNull(cmdLine);
    Preconditions.checkNotNull(out);
    Preconditions.checkNotNull(err);

    FileInputStream mdlInputStream = null;
    MapConfiguration config = generateAndValidateConfig(cmdLine);
    try {
      mdlInputStream =
          new FileInputStream(config.getString(OPT_INPUT_MDL.getLongOpt()));
      JsonMdlParser mdlParser = new JsonMdlParser();
      ServiceMonitoringDefinitionsDescriptor mdl =
          mdlParser.parse(IOUtils.toByteArray(mdlInputStream));
      ServiceMonitoringDefinitionsDescriptorImpl.Builder mdlBuilder =
          new ServiceMonitoringDefinitionsDescriptorImpl.Builder(mdl);

      MetricFixtureAdapter adapter = newMetricFixtureAdapter(config, out, err);
      adapter.init(config.getString(OPT_INPUT_FIXTURE.getLongOpt()),
                   config.getString(OPT_INPUT_CONVENTIONS.getLongOpt()));

      mdlBuilder.addMetricDefinitions(adapter.getServiceMetrics());

      if (null != mdl.getRoles()) {
        List<RoleMonitoringDefinitionsDescriptor> roles = Lists.newArrayList();
        for (RoleMonitoringDefinitionsDescriptor role : mdl.getRoles()) {
          RoleMonitoringDefinitionsDescriptorImpl.Builder roleBuilder =
              new RoleMonitoringDefinitionsDescriptorImpl.Builder(role);
          roleBuilder.addMetricDefinitions(
              adapter.getRoleMetrics(role.getName()));
          roles.add(roleBuilder.build());
        }
        mdlBuilder.setRoles(roles);
      }

      if (null != mdl.getMetricEntityTypeDefinitions()) {
        List<MetricEntityTypeDescriptor> entities = Lists.newArrayList();
        for (MetricEntityTypeDescriptor entity :
            mdl.getMetricEntityTypeDefinitions()) {
          MetricEntityTypeDescriptorImpl.Builder entityBuilder =
              new MetricEntityTypeDescriptorImpl.Builder(entity);
          entityBuilder.addMetricDefinitions(
              adapter.getEntityMetrics(entity.getName()));
          entities.add(entityBuilder.build());
        }
        mdlBuilder.setMetricEntityTypeDescriptor(entities);
      }
      FileUtils.write(new File(config.getString(OPT_GENERATE_OUPTUT.getLongOpt(),
                                                DEFAULT_OUTPUT_FILE)),
                      mdlParser.valueAsString(mdlBuilder.build(), true));
    } catch (Exception ex) {
      LOG.error("Could not run MetricGenerator tool.", ex);
      IOUtils.write(ex.getMessage() + "\n", err);
      throw ex;
    } finally {
      IOUtils.closeQuietly(mdlInputStream);
    }
  }

  private MetricFixtureAdapter newMetricFixtureAdapter(
      MapConfiguration config,
      OutputStream out,
      OutputStream err) throws IllegalAccessException, InstantiationException {
    Preconditions.checkNotNull(config);
    Preconditions.checkNotNull(out);
    Preconditions.checkNotNull(err);
    // All implementations of the MetricFixtureAdapter must have a no-argument
    // c'tor.
    LOG.info("Instantiating new metric fixture of class " +
             config.getString(OPT_ADAPTER_CLASS.getLongOpt()));
    Class<?> adapterClass = (Class<?>) config.getProperty(ADAPTER_CLASS_CONFIG);
    Preconditions.checkNotNull(adapterClass);
    return (MetricFixtureAdapter) adapterClass.newInstance();
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
      throw new ParseException("MetricGeneratorTool missing mdl file " +
                               "location");
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
      throw new ParseException("MetricGeneratorTool missing fixture file " +
                               "location");
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

    if (null != ret.getProperty(OPT_INPUT_CONVENTIONS.getLongOpt()) ) {
      String fileName = ret.getString(OPT_INPUT_CONVENTIONS.getLongOpt());
      File file = new File(fileName);
      if (!file.exists()) {
        throw new ParseException("Conventions file '" + fileName + "' does " +
                                 "not exist");
      } else if (!file.isFile()) {
         throw new ParseException("Conventions file '" + fileName + "' is " +
                                  "not a file");
      }
    }

    if (null == ret.getProperty(OPT_ADAPTER_CLASS.getLongOpt())) {
      throw new ParseException("MetricGeneratorTool missing adapter class");
    } else {
      String className = ret.getString(OPT_ADAPTER_CLASS.getLongOpt());
      try {
        Class<?> adapterClass =
            this.getClass().getClassLoader().loadClass(className);
        if (!MetricFixtureAdapter.class.isAssignableFrom(adapterClass)) {
          throw new ParseException("Adapter class " + className + "is of the " +
                                   "wrong type");
        }
        ret.addProperty(ADAPTER_CLASS_CONFIG, adapterClass);
      } catch (ClassNotFoundException e) {
        throw new ParseException("Unknown metric adapter " + className);
      }
    }
    return ret;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
