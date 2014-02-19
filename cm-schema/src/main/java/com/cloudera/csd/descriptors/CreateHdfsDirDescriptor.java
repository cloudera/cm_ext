// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.cloudera.validation.constraints.FilePermission;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/** Interface to specify command to create directory in HDFS. */
public interface CreateHdfsDirDescriptor {
  
  /** Name of the command. */
  @NotBlank
  String getName();
  
  /** Display name of the command. */
  @NotBlank
  String getLabel();
  
  /** Help string for the command. */
  @NotBlank
  String getDescription();

  /** Description for this directory. */
  @NotBlank
  String getDirectoryDescription();

  /** The path in HDFS to create. This can have standard substitutions. */
  @NotBlank
  String getPath();

  /** Permission for this directory. */
  @FilePermission
  String getPermissions();
}
