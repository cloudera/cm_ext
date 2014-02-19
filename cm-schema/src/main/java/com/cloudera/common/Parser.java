// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.common;

import java.io.IOException;

/**
 * Base interface for parsers that turn raw input into a validated, typed, form.
 */
public interface Parser<T> {

  /**
   * Reads the data and returns a parsed descriptor representation.
   *
   * @param data the data to parse
   * @return the parsed descriptor
   * @throws IOException if anything goes wrong with parsing.
   */
  T parse(byte[] data) throws IOException;
}
