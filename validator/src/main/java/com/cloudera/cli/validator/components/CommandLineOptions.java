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
package com.cloudera.cli.validator.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Encapsulates parsing the command line arguments.
 */
public class CommandLineOptions {

  private final CommandLine cmdLine;
  private final String appName;

  private static final Option SDL_FILE_OPTION = OptionBuilder.withLongOpt("sdl")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The SDL to validate")
      .isRequired(false)
      .create("s");

  private static final Option PARCEL_JSON_OPTION = OptionBuilder.withLongOpt("parcel-json")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The parcel.json file to validate")
      .isRequired(false)
      .create("p");

  private static final Option ALTERNATIVES_JSON_OPTION = OptionBuilder.withLongOpt("alternatives-json")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The alternatives.json file to validate")
      .isRequired(false)
      .create("a");

  private static final Option PERMISSIONS_JSON_OPTION = OptionBuilder.withLongOpt("permissions-json")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The permissions.json file to validate")
      .isRequired(false)
      .create("r");

  private static final Option MANIFEST_JSON_OPTION = OptionBuilder.withLongOpt("manifest-json")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The manifest.json file to validate")
      .isRequired(false)
      .create("m");

  private static final Option PARCEL_DIR_OPTION = OptionBuilder.withLongOpt("parcel-dir")
      .withArgName("DIRECTORY")
      .hasArg()
      .withDescription("The parcel directory to validate")
      .isRequired(false)
      .create("d");

  private static final Option PARCEL_FILE_OPTION = OptionBuilder.withLongOpt("parcel")
      .withArgName("FILE")
      .hasArg()
      .withDescription("The parcel file to validate")
      .isRequired(false)
      .create("f");

  public static enum Mode {
    SDL_FILE(SDL_FILE_OPTION, "sdlRunner"),
    PARCEL_JSON(PARCEL_JSON_OPTION, "parcelRunner"),
    ALTERNATIVES_JSON(ALTERNATIVES_JSON_OPTION, "alternativesRunner"),
    PERMISSIONS_JSON(PERMISSIONS_JSON_OPTION, "permissionsRunner"),
    MANIFEST_JSON(MANIFEST_JSON_OPTION, "manifestRunner"),
    PARCEL_DIR(PARCEL_DIR_OPTION, "parcelDirectoryRunner"),
    PARCEL_FILE(PARCEL_FILE_OPTION, "parcelFileRunner");

    private static final Map<Option, Mode> optionMap;
    static {
      ImmutableMap.Builder<Option, Mode> builder = ImmutableMap.builder();
      for (Mode e : Mode.values()) {
        builder.put(e.option, e);
      }
      optionMap = builder.build();
    }

    private final Option option;
    public final String runnerName;

    private static Mode getMode(Option option) {
      return optionMap.get(option);
    }

    Mode(Option option, String runnerName) {
      this.option = option;
      this.runnerName = runnerName;
    }

    public String getOpt() {
      return option.getOpt();
    }
  }

  private static final Options OPTIONS = new Options();
  static {
    for (Mode e : Mode.values()) {
      if (e.option != null) {
        OPTIONS.addOption(e.option);
      }
    }
  }

  /**
   * Creates the object from the command line arguments.
   *
   * @param appName the name of the application
   * @param args command line argument
   * @throws ParseException if there is an issue with parsing the arguments.
   */
  public CommandLineOptions(String appName, String[] args) throws ParseException {
    Preconditions.checkNotNull(appName);
    Preconditions.checkNotNull(args);
    CommandLineParser cliParser = new DefaultParser();
    this.appName = appName;
    this.cmdLine = cliParser.parse(OPTIONS, args);
  }

  public Mode getMode() {
    for (Option option : cmdLine.getOptions()) {
      Mode mode = Mode.getMode(option);
      if (mode != null) {
        return mode;
      }
    }
    return null;
  }

  public String getActiveTarget() {
    return cmdLine.getOptionValue(getMode().getOpt());
  }

  /**
   * Writes usage message to outputstream.
   *
   * @param appName the application name.
   * @param stream output stream.
   */
  public static void printUsageMessage(String appName, OutputStream stream) {
    PrintWriter writer = new PrintWriter(stream);
    try {
      String header = "Validates Cloudera Manager Schemas";
      String footer = "";
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(writer,
                          HelpFormatter.DEFAULT_WIDTH,
                          appName,
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
}
