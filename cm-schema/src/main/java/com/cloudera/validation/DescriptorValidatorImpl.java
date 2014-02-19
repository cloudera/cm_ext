// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.validation;

import com.cloudera.validation.DescriptorValidator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * A class that implements the DescriptorValidator interface
 * using the bean validation (JSR 303) framework.
 */
public class DescriptorValidatorImpl<T> implements DescriptorValidator<T> {

  private final Validator validator;
  private final String errorPrefix;
  private final static String ERROR_FORMAT = "%s.%s %s";

  public DescriptorValidatorImpl(Validator validator, String errorPrefix) {
    this.validator = validator;
    this.errorPrefix = errorPrefix;
  }

  @VisibleForTesting
  public Set<ConstraintViolation<T>> getViolations(T descriptor) {
    return validator.validate(descriptor);
  }

  @Override
  public Set<String> validate(T descriptor) {
    Set<ConstraintViolation<T>> constraintViolations;
    constraintViolations = getViolations(descriptor);

    ImmutableSet.Builder<String> violations = ImmutableSet.builder();
    for (ConstraintViolation<T> violation : constraintViolations) {
      String message = violation.getMessage();
      String relativePath = violation.getPropertyPath().toString();
      violations.add(String.format(ERROR_FORMAT, errorPrefix, relativePath, message));
    }
    return violations.build();
  }
}
