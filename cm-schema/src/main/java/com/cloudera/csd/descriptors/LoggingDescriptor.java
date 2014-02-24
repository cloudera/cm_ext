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

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes the logging context.
 */
public interface LoggingDescriptor {

  /**
   * The directory should have mode of 0755.
   *
   * @return the directory path containing all relevant log files
   */
  @NotBlank
  String getDir();

  /**
   * Filename of the log file. If the filename contains the string
   * "${host}", it gets replaced with hostname of the role.
   *
   * @return the filename
   */
  @NotBlank
  String getFilename();

  /**
   * Whether the directory should be exposed in CM UI for modification.
   *
   * @return true to be modifiable. Defaults to false.
   */
  boolean isModifiable();
  
  /**
   * Logging type used by the entity using this descriptor.
   */
  CsdLoggingType getLoggingType();
  
  /**
   * Used as the property name of the log directory parameter while emitting
   * it in config files. If not specified, then "log_dir" is used.
   */
  String getConfigName();
}
