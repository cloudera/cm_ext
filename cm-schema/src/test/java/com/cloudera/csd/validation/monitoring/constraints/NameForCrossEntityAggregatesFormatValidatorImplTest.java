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

import com.cloudera.csd.validation.monitoring.components.NameForCrossEntityAggregatesFormatValidatorImpl;

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
public class NameForCrossEntityAggregatesFormatValidatorImplTest {
  @Mock
  private ConstraintValidatorContext context;

  @Spy
  @InjectMocks
  private NameForCrossEntityAggregatesFormatValidatorImpl validator;

  @NameForCrossEntityAggregatesFormat
  private String nameForCrossEntityAggregate;

  @Before
  public void setupMetricNameFormatValidatorImplTest() {
    try {
      Field field =
          NameForCrossEntityAggregatesFormatValidatorImplTest.class
              .getField("nameForCrossEntityAggregate");
      field.setAccessible(true);
      validator.initialize(field.getAnnotation(
          NameForCrossEntityAggregatesFormat.class));
    } catch (NoSuchFieldException e) {
    }
  }

  @Test
  public void testGoodName() {
    nameForCrossEntityAggregate = "name_for_aggregate";
    assertTrue(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testGoodNameWithNumbers() {
    nameForCrossEntityAggregate = "na12me_00a00ggrega092te8409_one11";
    assertTrue(validator.isValid(nameForCrossEntityAggregate, context));
    nameForCrossEntityAggregate = "na01973me_00a00ggrega092te8409_one1_1";
    assertTrue(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testNoUnderscore() {
    nameForCrossEntityAggregate = "nameforaggregate";
    assertTrue(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testEndingWithUnderscoreOneGroup() {
    nameForCrossEntityAggregate = "aggregate_";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testEndingWithUnderscoreMoreThanOneGroup() {
    nameForCrossEntityAggregate = "for_aggregate_";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testStartsWithANumber() {
    nameForCrossEntityAggregate = "7name_for_aggregate";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testHasAGroupThatStartsWithANumber() {
    nameForCrossEntityAggregate = "name_7for_aggregate";
    assertTrue(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testNoDoubleUnderscore() {
    nameForCrossEntityAggregate = "name__for_aggregate";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testNotEndingingUnderscore() {
    nameForCrossEntityAggregate = "name_for_aggregate_";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }

  @Test
  public void testNotStartingUnderscore() {
    nameForCrossEntityAggregate = "_name_for_aggregate";
    assertFalse(validator.isValid(nameForCrossEntityAggregate, context));
  }
}
