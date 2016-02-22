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

import com.cloudera.common.Parser;
import com.cloudera.config.DefaultValidatorConfiguration;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.tools.MetricTools.MetricTool;
import com.cloudera.validation.DescriptorRunner;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.ValidationRunner;
import com.google.common.base.Preconditions;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * MetricTool to validate an MDL file. The validations performed by this tool
 * match those performed by CM when loading the MDL.
 */
public class MetricDescriptorValidatorTool implements MetricTool {

  private static final Logger LOG = LoggerFactory.getLogger(
      MetricDescriptorValidatorTool.class);

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  private static final Option OPT_MDL = OptionBuilder
      .withLongOpt("mdl")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The monitoring definitions to validate.")
      .isRequired(false)
      .create();

  public static void addToolOptions(Options options) {
    options.addOption(OPT_MDL);
  }

  @Override
  public void run(CommandLine cmdLine, OutputStream out, OutputStream err)
      throws Exception {
    Preconditions.checkNotNull(cmdLine);
    Preconditions.checkNotNull(out);
    Preconditions.checkNotNull(err);

    Writer writer = new OutputStreamWriter(out, "UTF-8");
    try {
      ApplicationContext ctx = new AnnotationConfigApplicationContext(
          DefaultValidatorConfiguration.class);
      @SuppressWarnings("unchecked")
      Parser<ServiceMonitoringDefinitionsDescriptor> parser =
        ctx.getBean("mdlParser", Parser.class);
      @SuppressWarnings("unchecked")
      DescriptorValidator<ServiceMonitoringDefinitionsDescriptor> validator =
        ctx.getBean("serviceMonitoringDefinitionsDescriptorValidator",
                    DescriptorValidator.class);
      ValidationRunner runner =
          new DescriptorRunner<ServiceMonitoringDefinitionsDescriptor>(
              parser, validator);
      if (!runner.run(cmdLine.getOptionValue(OPT_MDL.getLongOpt()), writer)) {
        throw new RuntimeException("Validation failed.");
      }
    } catch (Exception ex) {
      LOG.error("Could not run validation tool.", ex);
      IOUtils.write(ex.getMessage() + "\n", err);
      throw ex;
    } finally {
      writer.close();
    }
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
