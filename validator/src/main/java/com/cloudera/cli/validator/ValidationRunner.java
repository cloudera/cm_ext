// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.cli.validator;

import java.io.IOException;
import java.io.Writer;

/**
 * Interface for a validation runner.
 *
 * All this interface assumes is that the validation target can be identified by
 * a single string.
 */
public interface ValidationRunner {

  /**
   * Run the validation for the specified target. If there are any violations,
   * print them to the passed in writer.
   *
   * @param target identifier for the validation target
   * @param writer to write validation errors to
   * @return true if validation passed, false otherwise
   * @throws IOException if we can't write to the outputStream
   */
  boolean run(String target, Writer writer) throws IOException;
}
