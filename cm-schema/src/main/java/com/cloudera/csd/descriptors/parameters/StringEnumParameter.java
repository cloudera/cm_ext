// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

public interface StringEnumParameter extends Parameter<String> {

  /** Set of values this parameter can take. (REQUIRED) */
  @NotEmpty
  Set<String> getValidValues();
}
