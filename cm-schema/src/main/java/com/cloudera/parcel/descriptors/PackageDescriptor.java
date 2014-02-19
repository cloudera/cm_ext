// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Descriptor for the 'packages' section of parcel.json
 */
public interface PackageDescriptor {

  @NotBlank
  String getName();

  @NotBlank
  String getVersion();
}
