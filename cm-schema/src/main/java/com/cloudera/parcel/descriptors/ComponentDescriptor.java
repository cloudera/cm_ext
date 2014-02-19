// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Descriptor for the 'components' section of parcel.json
 */
public interface ComponentDescriptor {

  @NotBlank
  String getName();

  @NotBlank
  String getVersion();

  String getPkg_version();

  String getPkg_release();
}
