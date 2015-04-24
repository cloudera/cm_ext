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
package com.cloudera.csd.validation.components;

import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.validation.constraints.ServiceDependencyValidationGroup;
import com.cloudera.csd.validation.references.ReferenceValidator;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.DescriptorValidatorImpl;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * A class that implements the DescriptorValidator interface
 * for ServiceDescriptor objects.
 */
public class ServiceDescriptorValidatorImpl extends
    DescriptorValidatorImpl<ServiceDescriptor> implements
    DescriptorValidator<ServiceDescriptor> {

  private final Validator validator;
  private final ReferenceValidator refValidator;
  private final boolean enforceDependencyCheck;
  private Set<String> dependencyViolationStringSet;

  public ServiceDescriptorValidatorImpl(
      Validator validator,
      ReferenceValidator refValidator,
      boolean enforceDependencyCheck) {
    super(validator, "service");
    this.validator = validator;
    this.refValidator = refValidator;
    this.enforceDependencyCheck = enforceDependencyCheck;
    this.dependencyViolationStringSet = Sets.newHashSet();
  }

  @VisibleForTesting
  public Set<ConstraintViolation<ServiceDescriptor>> getViolations(
      ServiceDescriptor descriptor) {
    Set<ConstraintViolation<ServiceDescriptor>> violations =
        validator.validate(descriptor);
    if (enforceDependencyCheck) {
      Set<ConstraintViolation<ServiceDescriptor>> dependencyViolations =
          validator.validate(
              descriptor,
              ServiceDependencyValidationGroup.class);
      for (ConstraintViolation<ServiceDescriptor> violation : dependencyViolations) {
        String message = violation.getMessage();
        String[] propertyPathSections =
            violation.getPropertyPath().toString().split("\\.");
        dependencyViolationStringSet.add(
            String.format("%s %s",
                propertyPathSections[propertyPathSections.length - 1],
                message));
      }
    }

    if (!violations.isEmpty()) {
      return violations;
    }
    return refValidator.validate(descriptor);
  }

  @Override
  public Set<String> validate(ServiceDescriptor descriptor) {
    Set<ConstraintViolation<ServiceDescriptor>> constraintViolations;
    constraintViolations = getViolations(descriptor);

    ImmutableSet.Builder<String> violations = ImmutableSet.builder();
    for (ConstraintViolation<ServiceDescriptor> violation : constraintViolations) {
      String message = violation.getMessage();
      String relativePath = violation.getPropertyPath().toString();
      violations.add(String.format("%s.%s %s", "service", relativePath, message));
    }
    violations.addAll(dependencyViolationStringSet);
    dependencyViolationStringSet.clear();
    return violations.build();
  }
}
