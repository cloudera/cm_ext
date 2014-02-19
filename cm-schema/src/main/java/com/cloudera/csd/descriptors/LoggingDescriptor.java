// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
