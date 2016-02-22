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

import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.MonitoringConventions;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class EntityParentsReferToExistingEntitiesValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private EntityParentsReferToExistingEntitiesValidator validator;
  private MonitoringValidationContext context;
  private MetricEntityTypeDescriptor entity;

  @Before
  public void setupEntityParentsReferToExistingEntitiesValidatorTest() {
    entity = mockEntity("foobar_entity_one");
    validator = new EntityParentsReferToExistingEntitiesValidator();
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
  public void testUnknownParent() {
    MetricEntityTypeDescriptor entity2 = mockEntity("foobar_entity_two");
    doReturn(ImmutableList.of("FOOBAR", "non_existing_parent")).when(entity)
        .getParentMetricEntityTypeNames();
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
                   "Unknown parent 'non_existing_parent' for metric entity " +
                   "type 'foobar_entity_one'"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.parentMetricEntityTypeNames", SERVICE_NAME),
                 path);
  }

  @Test
  public void testServiceParent() {
    doReturn(ImmutableList.of("FOOBAR")).when(entity)
        .getParentMetricEntityTypeNames();
    addEntity(entity);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }

  @Test
  public void testRoleParent() {
    doReturn(ImmutableList.of(
        "FOOBAR",
        MonitoringConventions.getRoleMetricEntityTypeName(
            serviceDescriptor.getName(),
            "ROLE1")))
        .when(entity)
        .getParentMetricEntityTypeNames();
    addEntity(entity);
    RoleMonitoringDefinitionsDescriptor role = mockRole("ROLE1");
    addRole(role);
    context = new MonitoringValidationContext(serviceDescriptor);
    assertTrue(validator.validate(context, entity, root).isEmpty());
  }
}
