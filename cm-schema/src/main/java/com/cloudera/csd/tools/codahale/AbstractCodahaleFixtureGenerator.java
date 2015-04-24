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
package com.cloudera.csd.tools.codahale;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;

/**
 * A generic codahale monitoring fixture generator application to take care of
 * the rote options handling.
 */
public abstract class AbstractCodahaleFixtureGenerator {

  protected static final String CODAHALE_OUT_DEFAULT_FILE_NAME =
      "codahale_monitoring_defintions.json";

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  protected static final Option OPT_GENERATED_OUPTUT = OptionBuilder
      .withLongOpt("out")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The generated codahale metric definition file name. By " +
                       "default codahale_monitoring_defintions.json will be used.")
      .isRequired(false)
      .create("o");

  protected static final Options DEFAULT_OPTIONS;

  static {
    DEFAULT_OPTIONS = new Options();
    DEFAULT_OPTIONS.addOption(OPT_GENERATED_OUPTUT);
  }

  protected final MapConfiguration config;

  public AbstractCodahaleFixtureGenerator(String[] args) throws Exception {
    this(args, DEFAULT_OPTIONS);
  }

  public AbstractCodahaleFixtureGenerator(String[] args,
                                          Options options) throws Exception {
    Preconditions.checkNotNull(args);
    Preconditions.checkNotNull(options);

    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmdLine = parser.parse(options, args);
      if (!cmdLine.getArgList().isEmpty()) {
        throw new ParseException("Unexpected extra arguments: " +
                                 cmdLine.getArgList());
      }
      config = new MapConfiguration(Maps.<String, Object>newHashMap());
      for (Option option : cmdLine.getOptions()) {
        config.addProperty(option.getLongOpt(), option.getValue());
      }
    } catch (ParseException ex) {
      IOUtils.write("Error: " + ex.getMessage() + "\n", System.err);
      printUsageMessage(System.err, options);
      throw ex;
    }
  }

  /**
   * Generate the fixture.
   */
  public abstract CodahaleMetricDefinitionFixture generateFixture()
      throws Exception;

  /**
   * Writes usage message to 'stream'.
   *
   * @param stream output stream.
   * @throws UnsupportedEncodingException
   */
  public static void printUsageMessage(OutputStream stream, Options options)
      throws UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(stream, "UTF-8")));
    try {
      String header = "Mgmt Metric Schema Generator";
      String footer = "";
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(writer,
                          HelpFormatter.DEFAULT_WIDTH,
                          "schema generator",
                          header,
                          options,
                          HelpFormatter.DEFAULT_LEFT_PAD,
                          HelpFormatter.DEFAULT_DESC_PAD,
                          footer,
                          true); // auto-usage: whether to also show
                                 // the command line args on the usage line.
    } finally {
      writer.close();
    }
  }
}
