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

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;

import com.google.common.collect.Lists;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A base class for monitoring validators.
 */
abstract public class AbstractMonitoringValidatorBaseTest {
  protected static final String SERVICE_NAME = "FoOBaR";
  protected static ServiceMonitoringDefinitionsDescriptor serviceDescriptor;

  protected MetricDescriptor metric;
  protected DescriptorPathImpl root;
  protected List<RoleMonitoringDefinitionsDescriptor> roles;
  protected List<MetricEntityTypeDescriptor> entities;
  protected List<MetricEntityAttributeDescriptor> attributes;

  @BeforeClass
  public static void setup() {
    serviceDescriptor = mock(ServiceMonitoringDefinitionsDescriptor.class);
    doReturn(SERVICE_NAME).when(serviceDescriptor).getName();
  }

  @Before
  public void setUpAbstractMonitoringValidatorBaseTest() {
    metric = mock(MetricDescriptor.class);
    root = new DescriptorPathImpl();
    root = root.addBeanNode(serviceDescriptor);
    roles = Lists.newArrayList();
    entities = Lists.newArrayList();
    attributes = Lists.newArrayList();
    doReturn(roles).when(serviceDescriptor).getRoles();
    doReturn(entities).when(serviceDescriptor)
        .getMetricEntityTypeDefinitions();
    doReturn(attributes).when(serviceDescriptor)
        .getMetricEntityAttributeDefinitions();
  }

  protected void setName(String name) {
    doReturn(name).when(metric).getName();
  }

  protected void setIsCounter(boolean isCounter) {
    doReturn(isCounter).when(metric).isCounter();
  }

  protected void setDenominator(String denominator) {
    doReturn(denominator).when(metric).getDenominatorUnit();
  }

  protected void setWeightingMetric(String weightingMetric) {
    doReturn(weightingMetric).when(metric).getWeightingMetricName();
  }

  protected void setServiceMetrics(List<MetricDescriptor> metrics) {
    doReturn(metrics).when(serviceDescriptor).getMetricDefinitions();
  }

  protected MetricDescriptor newMetricWithName(String name) {
    MetricDescriptor ret = mock(MetricDescriptor.class);
    doReturn(name).when(ret).getName();
    return ret;
  }

  protected void setServiceNameForCrossEntityAggregates(String name) {
    doReturn(name).when(serviceDescriptor)
        .getNameForCrossEntityAggregateMetrics();
  }

  protected void addNameForCrossEntityAggregatesForRole(
      String roleName,
      String nameForAggregates) {
    RoleMonitoringDefinitionsDescriptor role =
        mock(RoleMonitoringDefinitionsDescriptor.class);
    doReturn(roleName).when(role).getName();
    doReturn(nameForAggregates).when(role)
        .getNameForCrossEntityAggregateMetrics();
    roles.add(role);
  }

  protected void addNameForCrossEntityAggregatesForEntity(
      String entityName,
      String nameForAggregates) {
    MetricEntityTypeDescriptor entity =
        mock(MetricEntityTypeDescriptor.class);
    doReturn(entityName).when(entity).getName();
    doReturn(nameForAggregates).when(entity)
        .getNameForCrossEntityAggregateMetrics();
    entities.add(entity);
  }

  protected MetricEntityTypeDescriptor mockEntity(String name) {
    MetricEntityTypeDescriptor ret = mock(MetricEntityTypeDescriptor.class);
    doReturn(name).when(ret).getName();
    return ret;
  }

  protected RoleMonitoringDefinitionsDescriptor mockRole(String name) {
    RoleMonitoringDefinitionsDescriptor ret =
        mock(RoleMonitoringDefinitionsDescriptor.class);
    doReturn(name).when(ret).getName();
    return ret;
  }

  protected void addEntity(MetricEntityTypeDescriptor entity) {
    entities.add(entity);
    doReturn(entities).when(serviceDescriptor)
        .getMetricEntityTypeDefinitions();
  }

  protected void addRole(RoleMonitoringDefinitionsDescriptor role) {
    roles.add(role);
    doReturn(roles).when(serviceDescriptor)
        .getRoles();
  }

  protected MetricEntityAttributeDescriptor mockAttribute(String name) {
    MetricEntityAttributeDescriptor ret =
        mock(MetricEntityAttributeDescriptor.class);
    doReturn(name).when(ret).getName();
    return ret;
  }

  protected void addAttribute(MetricEntityAttributeDescriptor attribute) {
    attributes.add(attribute);
    doReturn(attributes).when(serviceDescriptor)
        .getMetricEntityAttributeDefinitions();
  }

}
