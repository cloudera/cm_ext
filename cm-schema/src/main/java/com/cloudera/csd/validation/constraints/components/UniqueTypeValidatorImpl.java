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

import com.cloudera.csd.validation.constraints.UniqueType;
import com.cloudera.csd.validation.constraints.UniqueTypeValidator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

/**
 * A constraint validator that verifies all the runtime class types present in the validating
 * {@link java.util.Collection} is unique. This validator returns a constraint
 * violation if there are any duplicate class types in the collection.
 */
public class UniqueTypeValidatorImpl implements UniqueTypeValidator {

  private UniqueType uniqueType;
  private boolean skipNulls;

  @Override
  public void initialize(UniqueType uniqueType) {
    this.uniqueType = uniqueType;
    this.skipNulls = this.uniqueType.skipNulls();
  }

  @Override
  public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {

    if (list == null) {
      return true;
    }

    Set<Class<?>> seenSoFar = Sets.newHashSet();
    for (Object obj : list) {
      Class<?> type = classOf(obj);
      if ((type == null) && this.skipNulls) {
        continue;
      }
      if (seenSoFar.contains(type)) {
        addViolation(context);
        return false;
      }
      seenSoFar.add(type);
    }

    return true;
  }

  /**
   * Adds a violation in the context.
   *
   * @param context the context
   */
  @VisibleForTesting
  protected void addViolation(ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addConstraintViolation();
  }

  /**
   * Returns the runtime class for the given object.
   *
   * @param obj the object
   * @return If {@code obj} is not null, then return the runtime {@code Class} for the object.
   * Else, null.
   */
  @VisibleForTesting
  protected Class<?> classOf(Object obj) {
    if (obj != null) {
      return obj.getClass();
    }

    return null;
  }
}
