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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class NameForCrossEntityAggregatesPrefixedWithServiceNameValidatorTest
    extends AbstractMonitoringValidatorBaseTest {
  private static final Boolean SERVICE_NODE = true;
  private NameForCrossEntityAggregatesPrefixedWithServiceNameValidator validator;
  private MonitoringValidationContext context;

  @Before
  public void setupNameForCrossEntityAggregatesPrefixedWithServiceNameValidatorTest() {
    validator = new NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
        ImmutableSet.<String>of(), !SERVICE_NODE);
    context = new MonitoringValidationContext(serviceDescriptor);
  }

  @Test
  public void testNullName() {
    assertTrue(validator.validate(context, null, root).isEmpty());
  }

  @Test
  public void testEmptyName() {
    DescriptorPathImpl path = AbstractMonitoringValidator.getPathFromProperty(
        serviceDescriptor,
        "nameForCrossEntityAggregateMetrics",
        root);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, "", path);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "does not start with the service name"));
    String validatorPath = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.nameForCrossEntityAggregateMetrics",
                               SERVICE_NAME), validatorPath);
  }

  @Test
  public void testBadName() {
    DescriptorPathImpl path = AbstractMonitoringValidator.getPathFromProperty(
        serviceDescriptor,
        "nameForCrossEntityAggregateMetrics",
        root);
    List<ConstraintViolation<Object>> validations = validator.validate(
        context,
        SERVICE_NAME.toLowerCase().substring(1),
        path);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "does not start with the service name"));
    String validatorPath = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.nameForCrossEntityAggregateMetrics",
                               SERVICE_NAME), validatorPath);
  }

  @Test
  public void testMissingUnderscoreInNonServiceMode() {
    DescriptorPathImpl path = AbstractMonitoringValidator.getPathFromProperty(
        serviceDescriptor,
        "nameForCrossEntityAggregateMetrics",
        root);
    List<ConstraintViolation<Object>> validations = validator.validate(
        context,
        SERVICE_NAME.toLowerCase(),
        path);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "does not start with the service name"));
    String validatorPath = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.nameForCrossEntityAggregateMetrics",
                               SERVICE_NAME), validatorPath);
  }

  @Test
  public void testMissingUnderscoreInServiceMode() {
    validator = new NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
        ImmutableSet.<String>of(),
        SERVICE_NODE);
    assertTrue(validator.validate(
        context, SERVICE_NAME.toLowerCase(), root).isEmpty());
  }

  @Test
  public void testGoodName() {
    assertTrue(validator.validate(
        context, SERVICE_NAME.toLowerCase() + "_", root).isEmpty());
  }

  @Test
  public void testGoodLongName() {
    assertTrue(validator.validate(
        context, SERVICE_NAME.toLowerCase() + "_foo_bar", root).isEmpty());
  }

  @Test
  public void testBuiltInsNames() {
    validator = new NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
        ImmutableSet.of(SERVICE_NAME.toLowerCase().substring(1)),
        SERVICE_NODE);
    assertTrue(validator.validate(
        context, SERVICE_NAME.toLowerCase().substring(1), root).isEmpty());
  }
}
