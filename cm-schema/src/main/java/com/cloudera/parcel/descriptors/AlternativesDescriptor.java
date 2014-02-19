// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Descriptor for the alternatives.json file.
 */
public interface AlternativesDescriptor {

  @Valid
  @NotNull
  public Map<String, AlternativeDescriptor> getAlternatives();
}
