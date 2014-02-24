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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.cloudera.csd.validation.constraints.Expression;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.expression.spel.SpelEvaluationException;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionValidatorImplTest {

  @Expression("field1 < field2 and field2 < field3")
  public static class TestBean {
    public String field1;
    public String field2;
    public String field3;

    public TestBean(String field1, String field2, String field3) {
      this.field1 = field1;
      this.field2 = field2;
      this.field3 = field3;
    }

    public String getField1() {
      return field1;
    }

    public String getField2() {
      return field2;
    }

    public String getField3() {
      return field3;
    }
  }

  @Expression("garbage123")
  public static class BadExpressionBean {}

  @Expression("field1 < 5")
  public static class NotComparableBean {
    public Collection<String> field1; // not Comparable

    public NotComparableBean(Collection<String> field1) {
      this.field1 = field1;
    }

    public Collection<String> getField1() {
      return field1;
    }
  }

  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private ExpressionValidatorImpl validator;

  @Before
  public void setupMocking() {
    doNothing().when(validator).addViolation(eq(context), anyString());
  }

  @Test
  public void testSatisfied() {
    validator.initialize(TestBean.class.getAnnotation(Expression.class));
    assertTrue(validator.isValid(new TestBean("a", "b", "c"), context));
    verify(validator, never()).addViolation(eq(context), anyString());
  }

  @Test
  public void testNotSatisfied() {
    validator.initialize(TestBean.class.getAnnotation(Expression.class));
    assertFalse(validator.isValid(new TestBean("a", "a", "a"), context));
    verify(validator).addViolation(eq(context), anyString());
  }

  @Test(expected=SpelEvaluationException.class)
  public void testBadExpression() {
    validator.initialize(BadExpressionBean.class.getAnnotation(Expression.class));
    validator.isValid(new BadExpressionBean(), context);
  }

  @Test(expected=SpelEvaluationException.class)
  public void testNotComparable() {
    validator.initialize(NotComparableBean.class.getAnnotation(Expression.class));
    validator.isValid(new NotComparableBean(ImmutableList.of("abc")), context);
  }
}
