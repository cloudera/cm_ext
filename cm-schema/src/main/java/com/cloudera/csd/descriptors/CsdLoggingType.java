// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

public enum CsdLoggingType {
  /**
   * CM auto-generates the following ParamSpecs for a role that uses this method:
   * <ol>
   * <li>Log Threshold
   * <li>Max file size
   * <li>Max backup index size
   * <li>Log4j safety valve
   * </ol>
   * <p>
   * It also creates a ConfigFileGenerator for log4j.properties file for the role.
   */
  LOG4J,
  /** CM doesn't do anything automatically for this logging type. */
  OTHER;

  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
