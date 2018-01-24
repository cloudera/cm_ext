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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.csd.validation.constraints.UniqueType;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

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
public class UniqueTypeValidatorImplTest {

  @Mock
  private ConstraintViolationBuilder builder;

  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private UniqueTypeValidatorImpl validator;

  @UniqueType
  public List<Base> list1 = Lists.newArrayList();

  @UniqueType(skipNulls = false)
  public List<Base> list2 = Lists.newArrayList();

  @Before
  public void setupMocks() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  public void testDuplicate() {
    list1 = Lists.<Base>newArrayList(new Child1(), new Child1());
    validator.initialize(getAnnotation("list1"));
    assertFalse(validator.isValid(list1, context));
    verify(validator).addViolation(context);
  }

  @Test
  public void testNoDuplicate() {
    list1 = Lists.newArrayList(new Child1(), new Child2());
    validator.initialize(getAnnotation("list1"));
    assertTrue(validator.isValid(list1, context));
    verify(validator, never()).addViolation(context);
  }

  @Test
  public void testNullValuesSkipped() {
    list1 = Lists.<Base>newArrayList(null, null, new Child2());
    validator.initialize(getAnnotation("list1"));
    assertTrue(validator.isValid(list1, context));
    verify(validator, never()).addViolation(eq(context));
  }

  @Test
  public void testNullValuesNotSkipped() {
    list2 = Lists.<Base>newArrayList(null, null, new Child2());
    validator.initialize(getAnnotation("list2"));
    assertFalse(validator.isValid(list2, context));
    verify(validator).addViolation(eq(context));
  }

  private UniqueType getAnnotation(String propertyName) {
    try {
      Field field = UniqueTypeValidatorImplTest.class.getField(propertyName);
      field.setAccessible(true);
      return field.getAnnotation(UniqueType.class);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  private class Base {
  }

  private class Child1 extends Base {
  }

  private class Child2 extends Base {
  }
}
