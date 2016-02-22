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
package com.cloudera.csd.descriptors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

public enum CsdLoggingType {
  /**
   * CM auto-generates the following ParamSpecs for a daemon role that uses this method:
   * <ol>
   * <li>Log threshold
   * <li>Max file size
   * <li>Max backup index size
   * <li>Log4j safety valve
   * </ol>
   * <p>
   * CM auto-generates the following ParamSpecs for a gateway role that uses this method:
   * <ol>
   * <li>Log threshold
   * <li>Log4j safety valve
   * </ol>
   * <p>
   * It also creates a ConfigFileGenerator for log4j.properties file for the role.
   */
  LOG4J("log4j.properties"),
  /**
   * CM auto-generates the following ParamSpecs for a daemon role that uses this method:
   * <ol>
   * <li>Log threshold
   * <li>Max file size
   * <li>Max backup index size
   * <li>Logback XML override
   * </ol>
   * <p>
   * CM auto-generates the following ParamSpecs for a gateway role that uses this method:
   * <ol>
   * <li>Log threshold
   * <li>Logback XML override
   * </ol>
   * <p>
   * It also creates a ConfigFileGenerator for logback.xml file for the role.
   */
  LOGBACK("logback.xml"),
  /**
   * CM auto-generates some ParamSpecs for a daemon role that uses this method.
   * Each ParamSpec will automatically be emitted into the environment. The
   * ParamSpecs are listed below with their corresponding environment variable
   * names in parentheses:
   * <ol>
   * <li>Log directory (GLOG_log_dir)
   * <li>Minimum log level (GLOG_minloglevel)
   * <li>Maximum log level to buffer (GLOG_logbuflevel)
   * <li>Minimum log verbosity (GLOG_v)
   * <li>Maximum log size (GLOG_max_log_size)
   * </ol>
   * <p>
   * When using glog-based logging, {@link LoggingDescriptor#getFilename()}
   * must end in ".INFO".
   */
  GLOG(null),
  /** CM doesn't do anything automatically for this logging type. */
  OTHER(null);

  private final String defaultLogConfigFilename;

  private CsdLoggingType(String defaultLogConfigFilename) {
    this.defaultLogConfigFilename = defaultLogConfigFilename;
  }

  /**
   * @return the default logging configuration filename for this logging type,
   *         or null if it is unknown.
   */
  public String getDefaultLogConfigFilename() {
    return defaultLogConfigFilename;
  }

  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
