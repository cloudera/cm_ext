// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cloudera.validation;

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
