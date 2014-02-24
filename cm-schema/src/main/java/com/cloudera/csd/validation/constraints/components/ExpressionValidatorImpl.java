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

import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.validation.constraints.Expression;
import com.cloudera.csd.validation.constraints.ExpressionValidator;
import com.google.common.annotations.VisibleForTesting;

import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * The implementation of the ExpressionValidator
 */
public class ExpressionValidatorImpl implements ExpressionValidator {

  private static final ExpressionParser PARSER = new SpelExpressionParser();
  private Expression expression;

  @Override
  public void initialize(Expression constraintAnnotation) {
    this.expression = constraintAnnotation;
  }

  @Override
  public boolean isValid(Object bean, ConstraintValidatorContext context) {
    boolean result = PARSER
        .parseExpression(expression.value())
        .getValue(bean, Boolean.class);
    if (!result) {
      // XXX: Hacky way to get the "name" of the bean that didn't satisfy the
      // expression.
      //
      // Better to use a common interface or an annotation.
      String name = "";
      if (bean instanceof Parameter) {
        Parameter<?> p = (Parameter<?>) bean;
        name = p.getName();
      }
      addViolation(context, name);
    }
    return result;
  }

  /**
   * Adds a violation with the property name in the context.
   * @param context the context
   * @param property the property name
   */
  @VisibleForTesting
  void addViolation(ConstraintValidatorContext context, String property) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
           .addPropertyNode(property)
           .addConstraintViolation();
  }
}
