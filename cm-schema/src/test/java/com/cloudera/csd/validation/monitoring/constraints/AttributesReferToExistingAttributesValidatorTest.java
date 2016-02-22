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
import static org.mockito.Mockito.doReturn;

import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class AttributesReferToExistingAttributesValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private AttributesReferToExistingAttributesValidator validator;
  private MonitoringValidationContext context;
  private MetricEntityTypeDescriptor entity;

  @Before
  public void setUpAttributesReferToExistingAttributesValidatorTest() {
    MetricEntityAttributeDescriptor attr = mockAttribute("attr1");
    addAttribute(attr);
    attr = mockAttribute("attr2");
    addAttribute(attr);
    validator = new AttributesReferToExistingAttributesValidator(
        ImmutableSet.of("builtIn1", "builtIn2"));
    context = new MonitoringValidationContext(serviceDescriptor);
    entity = mockEntity("FOOBAR_ENTITY");
  }

  @Test
  public void testNoAttributes() {
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testImmutableBuiltInAttributes() {
    doReturn(ImmutableList.of("builtIn1", "builtIn2")).when(entity)
        .getImmutableAttributeNames();
    addEntity(entity);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testImmutableDefinedAttributes() {
    doReturn(ImmutableList.of("attr1", "attr2", "builtIn1", "builtIn2"))
        .when(entity).getImmutableAttributeNames();
    addEntity(entity);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testMutableBuiltInAttributes() {
    doReturn(ImmutableList.of("builtIn1", "builtIn2")).when(entity)
        .getMutableAttributeNames();
    addEntity(entity);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testMutableDefinedAttributes() {
    doReturn(ImmutableList.of("attr1", "attr2", "builtIn1", "builtIn2"))
        .when(entity).getMutableAttributeNames();
    addEntity(entity);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testNonExistingMutableAttributes() {
    doReturn(ImmutableList.of("nonExistingAttribute", "attr2" ))
        .when(entity).getMutableAttributeNames();
    doReturn(ImmutableList.of("builtIn1", "builtIn2", "attr1")).when(entity)
        .getImmutableAttributeNames();
    addEntity(entity);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, entity, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "Unknown attribute 'nonExistingAttribute' for metric " +
                   "entity type 'FOOBAR_ENTITY'."));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.mutableAttributeNames", SERVICE_NAME), path);
  }

  @Test
  public void testNonExistingImmutableAttributes() {
    doReturn(ImmutableList.of("nonExistingAttribute", "attr2" ))
        .when(entity).getImmutableAttributeNames();
    doReturn(ImmutableList.of("builtIn1", "builtIn2", "attr1")).when(entity)
        .getMutableAttributeNames();
    addEntity(entity);
    List<ConstraintViolation<Object>> validations = validator.validate(
        context, entity, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "Unknown attribute 'nonExistingAttribute' for metric " +
                   "entity type 'FOOBAR_ENTITY'."));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.immutableAttributeNames", SERVICE_NAME), path);
  }
}
