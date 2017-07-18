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

import com.cloudera.csd.validation.constraints.UniqueField;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.List;
import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UniqueFieldValidatorImplTest {

  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private UniqueFieldValidatorImpl validator;

  @UniqueField( "field1")
  public List<TestBean> beans = ImmutableList.of();

  @UniqueField(value = "field1", skipNulls = false)
  public List<TestBean> beans2 = ImmutableList.of();

  @Test
  public void testDuplicate() {
    beans = ImmutableList.of(new TestBean("a", "b"), new TestBean("a", "b"));
    doNothing().when(validator).addViolation(eq(context), anyString());
    validator.initialize(getAnnotation("beans"));
    assertFalse(validator.isValid(beans, context));
    verify(validator).addViolation(context, "field1");
  }

  @Test
  public void testNoDuplicate() {
    beans = ImmutableList.of(new TestBean("aa", "b"), new TestBean("a", "b"));
    doNothing().when(validator).addViolation(eq(context), anyString());
    validator.initialize(getAnnotation("beans"));
    assertTrue(validator.isValid(beans, context));
    verify(validator, never()).addViolation(eq(context), anyString());
  }

  @Test
  public void testNullValuesSkipped() {
    beans = ImmutableList.of(new TestBean(null, "b"), new TestBean(null, "b"));
    doNothing().when(validator).addViolation(eq(context), anyString());
    validator.initialize(getAnnotation("beans"));
    assertTrue(validator.isValid(beans, context));
    verify(validator, never()).addViolation(eq(context), anyString());
  }

  @Test
  public void testNullValuesNotSkipped() {
    beans2 = ImmutableList.of(new TestBean(null, "b"), new TestBean(null, "b"));
    doNothing().when(validator).addViolation(eq(context), anyString());
    validator.initialize(getAnnotation("beans2"));
    assertFalse(validator.isValid(beans2, context));
    verify(validator).addViolation(context, "field1");
  }

  @Test
  public void testNullCollection() {
    validator.initialize(getAnnotation("beans"));
    assertTrue(validator.isValid(null, context));
  }

  @Test
  public void testPropertyValue() {
    TestBean bean = new TestBean("a", "b");
    Object value = validator.propertyValue(bean, "field1", false);
    assertEquals("a", value);
  }

  private UniqueField getAnnotation(String fieldName) {
    try {
      Field field = UniqueFieldValidatorImplTest.class.getField(fieldName);
      field.setAccessible(true);
      return field.getAnnotation(UniqueField.class);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  public static class TestBean {
    public String field1;
    public String field2;

    public TestBean(String field1, String field2) {
      this.field1 = field1;
      this.field2 = field2;
    }

    public String getField1() {
      return field1;
    }

    public String getField2() {
      return field2;
    }
  }

}
