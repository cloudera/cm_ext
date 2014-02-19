// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.UniqueRoleType;
import com.cloudera.csd.validation.constraints.UniqueRoleTypeValidator;

import java.util.Set;

import javax.validation.ConstraintValidatorContext;

/**
 * Implementation of UniqueRoleType constraint based
 * on a static list of role types.
 */
public class UniqueRoleTypeValidatorImpl implements UniqueRoleTypeValidator {

  private final Set<String> builtInRoleTypes;

  public UniqueRoleTypeValidatorImpl(Set<String> builtInRoleTypes) {
    this.builtInRoleTypes = builtInRoleTypes;
  }

  @Override
  public void initialize(UniqueRoleType constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !builtInRoleTypes.contains(value);
  }
}
