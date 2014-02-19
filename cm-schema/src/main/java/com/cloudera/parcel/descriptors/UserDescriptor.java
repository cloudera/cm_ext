// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Descriptor for the 'users' section of parcel.json
 */
public interface UserDescriptor {

  @NotBlank
  String getLongname();

  @NotBlank
  String getHome();

  @NotBlank
  String getShell();

  @NotNull
  Set<String> getExtra_groups();
}
