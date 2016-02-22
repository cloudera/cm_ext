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
package com.cloudera.csd.validation.monitoring;

import com.cloudera.csd.validation.monitoring.constraints.MonitoringConstraintViolation;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.cloudera.csd.validation.references.components.ReflectionHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * An abstract class for service monitoring definitions validators.
 */
public abstract class AbstractMonitoringValidator<V> {

  /**
   * Returns a string describing what the validator validates to be used in
   * documentation.
   * @return
   */
  public abstract String getDescription();

  /**
   * Validate the node returning a list of non-empty ConstraintViolation<T> if a
   * validation failure was encountered, empty list if no violations were found.
   *
   * Implementations can assume that they are called iff all annotation based
   * validations have passed. For example, implementations do not need to check
   * if 'node' is 'null' if 'node' is annotated with @NotNull.
   * @param context
   * @param node
   * @param path
   * @return
   */
  public abstract <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      V node,
      DescriptorPathImpl path);

  /**
   * Readability method.
   * @param <T>
   * @return
   */
  protected static <T> ImmutableList<T> noViolations() {
    return ImmutableList.of();
  }

  /**
   * Return an adjusted path for the property being validated for the node.
   * @param node
   * @param propertyName
   * @param path
   * @return
   */
  protected DescriptorPathImpl constructPathFromProperty(
      V node,
      String propertyName,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(propertyName);
    Preconditions.checkNotNull(path);
    return AbstractMonitoringValidator.getPathFromProperty(node,
                                                           propertyName,
                                                           path);
  }

  /**
   * Returns an immutable list containing exactly one violation for the bean
   * 'currentBean' with value 'currentValue' at path 'path' with 'message'.
   * @param message
   * @param currentBean
   * @param currentValue
   * @param path
   * @param <T>
   * @param <V>
   * @return
   */
  protected static <T, V> List<ConstraintViolation<T>> forViolation(
      String message,
      V currentBean,
      Object currentValue,
      DescriptorPathImpl path) {
    ConstraintViolation<T> constraintViolation =
          MonitoringConstraintViolation.forViolation(message,
                                                     currentBean,
                                                     currentValue,
                                                     path);
      return ImmutableList.of(constraintViolation);
  }

  /**
   * Return an adjusted path for the property being validated for the node.
   * @param node
   * @param propertyName
   * @param path
   * @return
   */
  public static <T> DescriptorPathImpl getPathFromProperty(
      T node,
      String propertyName,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(propertyName);
    Preconditions.checkNotNull(path);
    Method methodOfPropertyValidated =
        ReflectionHelper.propertyGetter(node, propertyName);
    Preconditions.checkNotNull(methodOfPropertyValidated);
    return path.addPropertyNode(methodOfPropertyValidated, false);
  }

  public static <T> void safeAddAllToCollection(Collection<T> target,
                                                Collection<T> source) {
    Preconditions.checkNotNull(target);
    if (null != source) {
      target.addAll(source);
    }
  }
}
