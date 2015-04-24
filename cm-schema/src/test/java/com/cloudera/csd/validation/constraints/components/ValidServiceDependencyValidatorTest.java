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

import com.cloudera.csd.validation.constraints.ValidServiceDependencyValidator;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidServiceDependencyValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  private ValidServiceDependencyValidator validator;

  @Mock
  ConstraintViolationBuilder contextBuilder;

  @Mock
  NodeBuilderCustomizableContext contextBuilderContext;

  @Mock
  ConstraintValidatorContext constraintValidatorContext;

  @Before
  public void setup() throws Exception {
    doNothing().when(context).disableDefaultConstraintViolation();
    when(context.getDefaultConstraintMessageTemplate()).thenReturn("foobar");
    when(context.buildConstraintViolationWithTemplate(eq("foobar")))
      .thenReturn(contextBuilder);
    when(contextBuilder.addPropertyNode(anyString()))
      .thenReturn(contextBuilderContext);
    when(contextBuilderContext.addConstraintViolation())
      .thenReturn(constraintValidatorContext);
  }

  @Test
  public void testServiceTypeIsNotValid() {
    Set<String> services = ImmutableSet.of("foo", "bar");
    validator = new ValidServiceDependencyValidatorImpl(services);
    assertFalse(validator.isValid("fuz", context));
  }

  @Test
  public void testServiceTypeIsValid() {
    Set<String> services = ImmutableSet.of("foo", "bar");
    validator = new ValidServiceDependencyValidatorImpl(services);
    assertTrue(validator.isValid("foo", context));
  }
}
