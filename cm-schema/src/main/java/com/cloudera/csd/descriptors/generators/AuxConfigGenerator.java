// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.generators;

import org.hibernate.validator.constraints.NotBlank;

/**
 * This config generator specifies arbitrary
 * configuration files to be created. The generator
 * either creates an empty configuration file or
 * copies the contents from another source file.
 * In either case, safety valves are automatically
 * created for files.
 */
public interface AuxConfigGenerator {

  /**
   * The name of the configuration file.
   */
  @NotBlank
  String getFilename();

  /**
   * The auxiliary file to copy when writing out
   * the configuration file. This is optional and
   * if not specified, an empty file is written out
   * unless the user has added to the safety valve
   * in Cloudera Manager.
   */
  String getSourceFilename();
}
