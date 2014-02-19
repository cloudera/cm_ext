// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.cli.validator;

import com.cloudera.cli.validator.components.CommandLineOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * The Validator Main
 */
public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private final OutputStream outStream;
  private final OutputStream errStream;
  private final String appName;

  /**
   * Create an application.
   */
  public Main(String appName, OutputStream outStream, OutputStream errStream) {
    this.outStream = outStream;
    this.errStream = errStream;
    this.appName = appName;
  }

  /**
   * From the arguments, run the validation.
   *
   * @param args command line arguments
   * @throws IOException anything goes wrong with streams.
   * @return exit code
   */
  public int run(String[] args) throws IOException {
    Writer writer = new OutputStreamWriter(outStream);
    try {
      CommandLineOptions cmdOptions = new CommandLineOptions(appName, args);
      CommandLineOptions.Mode mode = cmdOptions.getMode();
      if (mode == null) {
        throw new ParseException("No valid command line arguments");
      }
      ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
      ValidationRunner runner = ctx.getBean(mode.runnerName, ValidationRunner.class);
      boolean success = runner.run(cmdOptions.getActiveTarget(), writer);
      return success ? 0 : -1;
    } catch (ParseException e) {
      LOG.debug("Exception", e);
      IOUtils.write(e.getMessage() + "\n", errStream);
      CommandLineOptions.printUsageMessage(appName, errStream);
      return -2;
    } finally {
      writer.close();
    }
  }

  public static void main(String[] args) throws IOException {
    String appName = System.getProperty("app.name", "app");
    Main app = new Main(appName, System.out, System.err);
    int ret = app.run(args);
    System.exit(ret);
  }
}
