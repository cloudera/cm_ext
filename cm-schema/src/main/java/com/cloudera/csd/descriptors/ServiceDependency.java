// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.cloudera.csd.validation.constraints.ValidServiceDependency;

import org.hibernate.validator.constraints.NotBlank;

/** Interface to specify a service dependency. */
public interface ServiceDependency {

  @NotBlank
  @ValidServiceDependency
  String getName();

  boolean isRequired();
}
