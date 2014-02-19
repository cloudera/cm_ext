// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.UniqueServiceType;
import com.cloudera.csd.validation.constraints.UniqueServiceTypeValidator;

import java.util.Set;

import javax.validation.ConstraintValidatorContext;

/**
 * Implementation of UniqueServiceType constraint based
 * on a static list of service types.
 */
public class UniqueServiceTypeValidatorImpl implements UniqueServiceTypeValidator {

  private final Set<String> builtInServiceTypes;

  public UniqueServiceTypeValidatorImpl(Set<String> builtInServiceTypes) {
    this.builtInServiceTypes = builtInServiceTypes;
  }

  @Override
  public void initialize(UniqueServiceType constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !builtInServiceTypes.contains(value);
  }
}
