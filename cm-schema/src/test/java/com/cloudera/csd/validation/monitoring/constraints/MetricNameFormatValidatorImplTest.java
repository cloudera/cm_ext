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
package com.cloudera.csd.validation.monitoring.constraints;

import com.cloudera.csd.validation.monitoring.components.MetricNameFormatValidatorImpl;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MetricNameFormatValidatorImplTest {

  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private MetricNameFormatValidatorImpl validator;

  @MetricNameFormat
  private String metricName;

  @Before
  public void setupMetricNameFormatValidatorImplTest() {
    try {
      Field field =
          MetricNameFormatValidatorImplTest.class.getField("metricName");
      field.setAccessible(true);
      validator.initialize(field.getAnnotation(MetricNameFormat.class));
    } catch (NoSuchFieldException e) {
    }
  }

  @Test
  public void testGoodName() {
    metricName = "echo_metric_one";
    assertTrue(validator.isValid(metricName, context));
  }

  @Test
  public void testGoodNameWithNumbers() {
    metricName = "e0c111ho_00mj00etric0928409_one11";
    assertTrue(validator.isValid(metricName, context));
    metricName = "e0c111ho_metric0928409_one1_1";
    assertTrue(validator.isValid(metricName, context));
  }

  @Test
  public void testNoUnderscore() {
    metricName = "echometricone";
    assertTrue(validator.isValid(metricName, context));
  }

  @Test
  public void testEndingWithUnderscoreOneGroup() {
    metricName = "echo_";
    assertFalse(validator.isValid(metricName, context));
  }

  @Test
  public void testEndingWithUnderscoreMoreThanOneGroup() {
    metricName = "echo_foo_bar_";
    assertFalse(validator.isValid(metricName, context));
  }

  @Test
  public void testStartsWithANumber() {
    metricName = "7echo_metric_one";
    assertFalse(validator.isValid(metricName, context));
  }

  @Test
  public void testHasAGroupThatStartsWithANumber() {
    metricName = "echo_9metric_one";
    assertTrue(validator.isValid(metricName, context));
  }

  @Test
  public void testNoDoubleUnderscore() {
    metricName = "echo__two_underscores";
    assertFalse(validator.isValid(metricName, context));
  }

  @Test
  public void testNotEndingingUnderscore() {
    metricName = "echo_metric_one_";
    assertFalse(validator.isValid(metricName, context));
  }

  @Test
  public void testNotStartingUnderscore() {
    metricName = "_echo_metric_one";
    assertFalse(validator.isValid(metricName, context));
  }
}
