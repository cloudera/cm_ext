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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class ParentsAreReachableUsingAttributesValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private ParentsAreReachableUsingAttributesValidator validator;
  private MonitoringValidationContext context;
  private MetricEntityTypeDescriptor entity;

  @Before
  public void setupParentsAreReachableUsingAttributesValidatorTest() {
    entity = mockEntity("foobar_entity_one");
    validator = new ParentsAreReachableUsingAttributesValidator();
    context = new MonitoringValidationContext(serviceDescriptor);
  }

  @Test
  public void testNoEntities() {
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testEntitiesNoParents() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    addEntity(entity);
    addEntity(entity2);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testMissingParents() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("non_existing_parent")).when(entity)
        .getParentMetricEntityTypeNames();
    addEntity(entity);
    addEntity(entity2);
    context = new MonitoringValidationContext(serviceDescriptor);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testParentWithoutNameParts() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("foobar_entity_two")).when(entity)
        .getParentMetricEntityTypeNames();
    addEntity(entity);
    addEntity(entity2);
    context = new MonitoringValidationContext(serviceDescriptor);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testGoodParents() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("foobar_entity_two")).when(entity)
        .getParentMetricEntityTypeNames();
    doReturn(ImmutableList.of("attr1", "attr2")).when(entity2)
        .getEntityNameFormat();
    doReturn(ImmutableList.of("attr1", "attr3")).when(entity)
        .getImmutableAttributeNames();
    doReturn(ImmutableList.of("attr2")).when(entity).getMutableAttributeNames();
    addEntity(entity);
    addEntity(entity2);
    context = new MonitoringValidationContext(serviceDescriptor);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testMissingOneAttribute() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("foobar_entity_two")).when(entity)
        .getParentMetricEntityTypeNames();
    doReturn(ImmutableList.of("attr1", "attr2")).when(entity2)
        .getEntityNameFormat();
    doReturn(ImmutableList.of("attr1", "attr3")).when(entity)
        .getImmutableAttributeNames();
    addEntity(entity);
    addEntity(entity2);
    context = new MonitoringValidationContext(serviceDescriptor);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, entity, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "Metric entity type 'foobar_entity_one' does not have " +
                   "attribute 'attr2' to be able to construct the name of the " +
                   "parent 'foobar_entity_two'"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.parentMetricEntityTypeNames", SERVICE_NAME),
                 path);
  }

  @Test
  public void testMissingTwoAttribute() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("foobar_entity_two")).when(entity)
        .getParentMetricEntityTypeNames();
    doReturn(ImmutableList.of("attr1", "attr2")).when(entity2)
        .getEntityNameFormat();
    doReturn(ImmutableList.of("attr3")).when(entity)
        .getImmutableAttributeNames();
    addEntity(entity);
    addEntity(entity2);
    context = new MonitoringValidationContext(serviceDescriptor);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, entity, root);
    assertEquals(2, validations.size());
    for (ConstraintViolation<Object> validation : validations) {
      String attrName = null;
      if (validation.getMessage().contains("attr2")) {
        attrName = "attr2";
      } else if (validation.getMessage().contains("attr1")) {
        attrName = "attr1";
      }
      assertNotNull(attrName);
      assertTrue(validation.toString(),
           validation.getMessage().contains(String.format(
               "Metric entity type 'foobar_entity_one' does not have " +
               "attribute '%s' to be able to construct the name of the " +
               "parent 'foobar_entity_two'", attrName)));
      String path = validation.getPropertyPath().toString();
      assertEquals(String.format("%s.parentMetricEntityTypeNames",
                                 SERVICE_NAME),
                   path);
    }
  }
}
