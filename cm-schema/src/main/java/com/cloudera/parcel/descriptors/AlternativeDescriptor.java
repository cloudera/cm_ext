// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Descriptor for a single entry in the alternatives.json file.
 */
public interface AlternativeDescriptor {
  @NotBlank
  String getDestination();

  @NotBlank
  String getSource();

  @NotNull
  Integer getPriority();

  @NotNull
  Boolean getIsDirectory();
}
