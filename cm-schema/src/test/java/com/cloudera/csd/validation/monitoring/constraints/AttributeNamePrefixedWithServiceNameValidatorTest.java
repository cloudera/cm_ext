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

import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttributeNamePrefixedWithServiceNameValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private AttributeNamePrefixedWithServiceNameValidator validator;
  private MonitoringValidationContext context;

  @Before
  public void setupEntityNamePrefixedWithServiceNameValidator() {
    validator = new AttributeNamePrefixedWithServiceNameValidator(
        ImmutableSet.of("builtInAttribute"));
    context = new MonitoringValidationContext(serviceDescriptor);
  }

  @Test
  public void testValidEntity() {
    MetricEntityAttributeDescriptor attribute = mockAttribute("foobarAttribute");
    assertTrue(validator.validate(context, attribute, root).isEmpty());
  }

  @Test
  public void testEmptyName() {
    MetricEntityAttributeDescriptor attribute = mockAttribute("");
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, attribute, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "does not start with the service name"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }

  @Test
  public void testInvaidServiceNameNotSmallCapsPrefixed() {
    MetricEntityAttributeDescriptor attribute = mockAttribute("FOOBARAttribute");
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, attribute, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "Attribute 'FOOBARAttribute' does not start with the " +
                   "service name ('foobar')"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }

  @Test
  public void testInvaidEntityNotPrefixed() {
    MetricEntityAttributeDescriptor attribute = mockAttribute("attribute");
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, attribute, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "Attribute 'attribute' does not start with the " +
                   "service name ('foobar')"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }
  @Test
  public void testBuiltInType() {
    MetricEntityAttributeDescriptor attribute =
        mockAttribute("builtInAttribute");
    assertTrue(validator.validate(context, attribute, root).isEmpty());
  }
}
