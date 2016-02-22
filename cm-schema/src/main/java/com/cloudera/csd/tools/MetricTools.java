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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

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
import org.apache.commons.io.IOUtils;

/**
 * The MetricTools program offers various tools to help generate valid
 * MetricDescriptors for different entity types.
 */
public class MetricTools {

  private static final String GENERATE_METRIC_TOOL_NAME = "generate";
  private static final String VALIDATE_METRIC_TOOL_NAME = "validate";
  private static final String ADD_METRICS_METRIC_TOOL_NAME = "addMetrics";

  @SuppressWarnings({ "AccessStaticViaInstance", "static-access" })
  @VisibleForTesting
  static final Option OPT_TOOL = OptionBuilder.withLongOpt("tool-name")
      .withArgName("TOOL")
      .hasArg()
      .withDescription("The metric tool to run. Can be one of the following:\n" +
          "1) " + GENERATE_METRIC_TOOL_NAME + ": generate a " +
          "list of metric descriptors for review.\n" +
          "2) " + VALIDATE_METRIC_TOOL_NAME + ": validate " +
          "metric descriptors.\n" +
          "2) " + ADD_METRICS_METRIC_TOOL_NAME + ": add metrics to a particular " +
          "type from a file to an MDL.\n")
      .isRequired(true)
      .create('t');
  @VisibleForTesting
  static final Options OPTIONS;

  static {
    Options options = new Options();
    options.addOption(OPT_TOOL);
    MetricDescriptorGeneratorTool.addToolOptions(options);
    MetricDescriptorValidatorTool.addToolOptions(options);
    MetricDescriptorAddMetricsTool.addToolOptions(options);
    OPTIONS = options;
  }

  /**
   * Writes usage message to 'stream'.
   *
   * @param stream output stream.
   * @throws UnsupportedEncodingException
   */
  public static void printUsageMessage(OutputStream stream)
      throws UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(stream, "UTF-8")));
    try {
      String header = "Cloudera Manager Metric Tools";
      String footer = "";
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(writer,
                          HelpFormatter.DEFAULT_WIDTH,
                          "metric tools",
                          header,
                          OPTIONS,
                          HelpFormatter.DEFAULT_LEFT_PAD,
                          HelpFormatter.DEFAULT_DESC_PAD,
                          footer,
                          true); // auto-usage: whether to also show
                                 // the command line args on the usage line.
    } finally {
      writer.close();
    }
  }

  /**
   * An interface all metric tools need to implement.
   */
  public static interface MetricTool {

    /**
     * Run the tool using the command line arguments.
     * @param cmdLine
     * @param out
     * @param err
     */
    public void run(CommandLine cmdLine, OutputStream out, OutputStream err)
      throws Exception;

    /**
     * Return the name of the tool.
     * @return
     */
    public String getName();
  }

  /**
   * A class responsible for instantiating the tool and running it.
   */
  private static class Main {

    private final CommandLine cmdLine;

    public Main(CommandLine cmdLine) {
      Preconditions.checkNotNull(cmdLine);
      this.cmdLine = cmdLine;
    }

    public void run() throws Exception {
      String toolName = getToolName();
      MetricTool tool = newMetricTool(toolName);
      tool.run(cmdLine, System.out, System.err);
    }

    private MetricTool newMetricTool(String toolName) throws ParseException {
      Preconditions.checkNotNull(toolName);
      if (GENERATE_METRIC_TOOL_NAME.equals(toolName)) {
        return new MetricDescriptorGeneratorTool();
      } else if (VALIDATE_METRIC_TOOL_NAME.equals(toolName)) {
        return new MetricDescriptorValidatorTool();
      } else if (ADD_METRICS_METRIC_TOOL_NAME.equals(toolName)) {
        return new MetricDescriptorAddMetricsTool();
      } else {
        throw new ParseException("Unknown metric tool: " + toolName);
      }
    }

    private String getToolName() throws ParseException {
      for (Option option : cmdLine.getOptions()) {
        if (option.equals(OPT_TOOL)) {
          return option.getValue();
        }
      }
      // We should never get here as the parser of the command line would
      // have thrown an exception before for missing required argument.
      Preconditions.checkState(false);
      throw new ParseException("Metric tool name not found");
    }
  }

  public static void main(String[] args) throws Exception {
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmdLine = parser.parse(OPTIONS, args);
      if (!cmdLine.getArgList().isEmpty()) {
        throw new ParseException("Unexpected extra arguments: " +
                                 cmdLine.getArgList());
      }
      Main main = new Main(cmdLine);
      main.run();
    } catch (ParseException ex) {
      IOUtils.write("Error: " + ex.getMessage() + "\n", System.err);
      printUsageMessage(System.err);
      System.exit(1);
    }
  }
}
