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

import static com.cloudera.csd.validation.constraints.components.MutuallyExclusiveTypeValidatorImplTest.Definitions.baseList;
import static com.cloudera.csd.validation.constraints.components.MutuallyExclusiveTypeValidatorImplTest.Definitions.extendedList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.csd.validation.constraints.MutuallyExclusiveType;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MutuallyExclusiveTypeValidatorImplTest {

  @Mock
  private ConstraintViolationBuilder builder;

  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private MutuallyExclusiveTypeValidatorImpl validator;

  @Before
  public void setupMocks() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);
  }

  public static class Definitions {

    @MutuallyExclusiveType(types = {Child1Descriptor.class, Child2Descriptor.class})
    public static ImmutableList<Base> baseList = ImmutableList.of();

    @MutuallyExclusiveType(types = {Child1Descriptor.class, Child2Descriptor.class,
        Child3Descriptor.class})
    public static ImmutableList<Base> extendedList = ImmutableList.of();
  }

  @Test
  public void testInvalidExclusivity() {
    baseList = ImmutableList.<Base>of(new Child1(), new Child2());
    validator.initialize(getAnnotation("baseList"));
    assertFalse(validator.isValid(baseList, context));
    verify(validator).addViolation(eq(context));
  }

  @Test
  public void testInvalidExclusivityWithDuplicates() {
    baseList = ImmutableList.<Base>of(new Child1(), new Child1(), new Child2());
    validator.initialize(getAnnotation("baseList"));
    assertFalse(validator.isValid(baseList, context));
    verify(validator).addViolation(eq(context));
  }

  @Test
  public void testValidExclusivity() {
    baseList = ImmutableList.<Base>of(new Child1(), new Child1());
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(baseList, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testSingleElementCollection() {
    baseList = ImmutableList.<Base>of(new Child1());
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(baseList, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testOutsideExclusivityScope() {
    baseList = ImmutableList.<Base>of(new Child3());
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(baseList, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testMixedCollection() {
    baseList = ImmutableList.<Base>of(new Child1(), new Child3());
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(baseList, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testValidExtendedCollection() {
    extendedList = ImmutableList.<Base>of(new Child3(), new Child3());
    validator.initialize(getAnnotation("extendedList"));
    assertTrue(validator.isValid(extendedList, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testInvalidExtendedCollection() {
    extendedList = ImmutableList.<Base>of(new Child1(), new Child3());
    validator.initialize(getAnnotation("extendedList"));
    assertFalse(validator.isValid(extendedList, context));
    verify(validator).addViolation(eq(context));
  }

  @Test
  public void testNullCollection() {
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(null, context));
  }

  @Test
  public void testEmptyCollection() {
    validator.initialize(getAnnotation("baseList"));
    assertTrue(validator.isValid(ImmutableList.of(), context));
  }

  private MutuallyExclusiveType getAnnotation(String propertyName) {
    try {
      Field field = Definitions.class.getField(propertyName);
      field.setAccessible(true);
      return field.getAnnotation(MutuallyExclusiveType.class);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  private interface BaseDescriptor {
  }

  private interface Child1Descriptor extends BaseDescriptor {
  }

  private interface Child2Descriptor extends BaseDescriptor {
  }

  private interface Child3Descriptor extends BaseDescriptor {
  }

  private class Base {
  }

  private class Child1 extends Base implements Child1Descriptor {
  }

  private class Child2 extends Base implements Child2Descriptor {
  }

  private class Child3 extends Base implements Child3Descriptor {
  }
}
