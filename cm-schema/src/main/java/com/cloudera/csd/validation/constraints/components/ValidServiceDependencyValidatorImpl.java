// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.ValidServiceDependency;
import com.cloudera.csd.validation.constraints.ValidServiceDependencyValidator;

import java.util.Set;
import javax.validation.ConstraintValidatorContext;

/**
 * Implementation of ValidServiceDependency constraint based
 * on a static list of dependencies.
 */
public class ValidServiceDependencyValidatorImpl implements ValidServiceDependencyValidator {

  private final Set<String> validServiceTypes;

  public ValidServiceDependencyValidatorImpl(Set<String> validServiceTypes) {
    this.validServiceTypes = validServiceTypes;
  }

  @Override
  public void initialize(ValidServiceDependency constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return validServiceTypes.contains(value);
  }
}
