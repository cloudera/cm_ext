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
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.descriptors.parameters.MemoryParameter;
import com.cloudera.csd.validation.constraints.AutoConfigSharesValid;
import com.cloudera.csd.validation.constraints.AutoConfigSharesValidValidator;
import com.google.common.annotations.VisibleForTesting;

import java.util.Collection;
import javax.validation.ConstraintValidatorContext;

/**
 * The implementation of the AutoConfigSharesValidValidator
 */
public class AutoConfigSharesValidValidatorImpl implements AutoConfigSharesValidValidator {

  @Override
  public void initialize(AutoConfigSharesValid constraintAnnotation) {}

  @Override
  public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {
    if (list == null) {
      return true;
    }
    boolean test = false;
    int total = 0;
    for (Object o : list) {
      if (o instanceof MemoryParameter) {
        MemoryParameter memory = (MemoryParameter) o;
        Integer share = memory.getAutoConfigShare();
        if (share != null) {
          total += share;
          test = true;
        }
      }
    }
    if (test && total != 100) {
      addViolation(context);
      return false;
    }
    return true;
  }

  /**
   * Adds a violation in the context.
   * @param context the context
   */
  @VisibleForTesting
  void addViolation(ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
           .addConstraintViolation();
  }
}
