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
import static org.junit.Assert.assertTrue;

import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class NameForCrossEntityAggregatesIsUniqueValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private NameForCrossEntityAggregatesIsUniqueValidator validator;
  private MonitoringValidationContext context;

  @Before
  public void setupNameForCrossEntityAggregatesIsUniqueValidatorTest() {
    validator = new NameForCrossEntityAggregatesIsUniqueValidator();
    context = new MonitoringValidationContext(serviceDescriptor);
  }

  @Test
  public void testEmptyMdl() {
    assertTrue(validator.validate(context, serviceDescriptor, root).isEmpty());
  }

  @Test
  public void testOnlyServiceHasNameForAggregates() {
    setServiceNameForCrossEntityAggregates("aggregate_for_service");
    assertTrue(validator.validate(context, serviceDescriptor, root).isEmpty());
  }

  @Test
  public void testUniqueAggregateNames() {
    setServiceNameForCrossEntityAggregates("aggregate_for_service");
    addNameForCrossEntityAggregatesForRole("role1", "aggregate_for_role1");
    addNameForCrossEntityAggregatesForRole("role2", "aggregate_for_role2");
    addNameForCrossEntityAggregatesForEntity("entity1", "aggregate_for_entity1");
    addNameForCrossEntityAggregatesForEntity("entity2", "aggregate_for_entity2");
    assertTrue(validator.validate(context, serviceDescriptor, root).isEmpty());
  }

  @Test
  public void testAllTheSameAggregateNames() {
    setServiceNameForCrossEntityAggregates("aggregate_for_service");
    addNameForCrossEntityAggregatesForRole("role1", "aggregate_for_service");
    addNameForCrossEntityAggregatesForRole("role2", "aggregate_for_service");
    addNameForCrossEntityAggregatesForEntity("entity1", "aggregate_for_service");
    addNameForCrossEntityAggregatesForEntity("entity2", "aggregate_for_service");
    List<ConstraintViolation<Object>> violations =
        validator.validate(context, serviceDescriptor, root);
    assertEquals(4, violations.size());
  }

  @Test
  public void testCollisionsBetweenRolesAggregateNames() {
    setServiceNameForCrossEntityAggregates("aggregate_for_service");
    addNameForCrossEntityAggregatesForRole("role1", "aggregate_for_role1");
    addNameForCrossEntityAggregatesForRole("role2", "aggregate_for_role1");
    addNameForCrossEntityAggregatesForEntity("entity1", "aggregate_for_entity1");
    addNameForCrossEntityAggregatesForEntity("entity2", "aggregate_for_entity2");
    List<ConstraintViolation<Object>> violations =
        validator.validate(context, serviceDescriptor, root);
    assertEquals(1, violations.size());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        violations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "name for cross entity aggregates is already in use"));
    String validatorPath = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.%s", SERVICE_NAME, "role2"), validatorPath);
  }

  @Test
  public void testCollisionsBetweenEntitiesAggregateNames() {
    setServiceNameForCrossEntityAggregates("aggregate_for_service");
    addNameForCrossEntityAggregatesForRole("role1", "aggregate_for_role1");
    addNameForCrossEntityAggregatesForRole("role2", "aggregate_for_role2");
    addNameForCrossEntityAggregatesForEntity("entity1", "aggregate_for_entity1");
    addNameForCrossEntityAggregatesForEntity("entity2", "aggregate_for_entity1");
    List<ConstraintViolation<Object>> violations =
        validator.validate(context, serviceDescriptor, root);
    assertEquals(1, violations.size());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        violations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "name for cross entity aggregates is already in use"));
    String validatorPath = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.%s", SERVICE_NAME, "entity2"), validatorPath);
  }
}
