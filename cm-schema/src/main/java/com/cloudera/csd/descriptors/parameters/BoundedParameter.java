// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

public interface BoundedParameter<T> extends Parameter<T> {

  /** Minimum value of this parameter. */
  T getMinValue();

  /** Maximum value of this parameter. */
  T getMaxValue();

  /** Unit of this parameter. */
  CsdParamUnits getUnit();
}
