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

import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * see getDescriptionForMoreDetails.
 *
 * Note that this class validates more than one node type so it's a bit weird
 * that we are using the AbstractMonitoringValidator - we do that for the
 * getDescription, i.e., auto-documentation.
 */
public class NameForCrossEntityAggregatesIsUniqueValidator
    extends AbstractMonitoringValidator<ServiceMonitoringDefinitionsDescriptor> {

  public NameForCrossEntityAggregatesIsUniqueValidator(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
    super(serviceDescriptor);
  }

  @Override
  public String getDescription() {
    return
        "Validates that all the names for cross entity aggregates are unique. " +
        "That is, no two entities (e.g., two roles, the service and an " +
        "entity) have the same name for cross entity aggregates.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(serviceDescriptor);
    Preconditions.checkNotNull(path);
    List<ConstraintViolation<T>> violations = Lists.newArrayList();

    Set<String> namesForCrossEntityAggregatesSeen = Sets.newHashSet();
    if (null != serviceDescriptor.getNameForCrossEntityAggregateMetrics()) {
      namesForCrossEntityAggregatesSeen.add(
          serviceDescriptor.getNameForCrossEntityAggregateMetrics());
    }

    if (null != serviceDescriptor.getRoles()) {
      for (RoleMonitoringDefinitionsDescriptor role :
           serviceDescriptor.getRoles()) {
        if (null != role.getNameForCrossEntityAggregateMetrics() &&
            namesForCrossEntityAggregatesSeen.contains(
                role.getNameForCrossEntityAggregateMetrics())) {
          path = path.addBeanNode(role);
          String msg = String.format(
            "'%s' name for cross entity aggregates is already in use",
            role.getNameForCrossEntityAggregateMetrics());
          violations.addAll(
              AbstractMonitoringValidator
                  .<T, RoleMonitoringDefinitionsDescriptor>forViolation(
                      msg,
                      role,
                      role.getNameForCrossEntityAggregateMetrics(),
                      path));
          path = path.removeFromHead();
        } else if (null != role.getNameForCrossEntityAggregateMetrics()) {
          namesForCrossEntityAggregatesSeen.add(
              role.getNameForCrossEntityAggregateMetrics());
        }
      }
    }

    if (null != serviceDescriptor.getMetricEntityTypeDefinitions()) {
      for (MetricEntityTypeDescriptor entity :
           serviceDescriptor.getMetricEntityTypeDefinitions()) {
        if (null != entity.getNameForCrossEntityAggregateMetrics() &&
            namesForCrossEntityAggregatesSeen.contains(
                entity.getNameForCrossEntityAggregateMetrics())) {
          path = path.addBeanNode(entity);
          String msg = String.format(
            "'%s' name for cross entity aggregates is already in use",
            entity.getNameForCrossEntityAggregateMetrics());
          violations.addAll(
              AbstractMonitoringValidator
                  .<T, MetricEntityTypeDescriptor>forViolation(
                msg,
                entity,
                entity.getNameForCrossEntityAggregateMetrics(),
                path));
          path = path.removeFromHead();
        } else if (null != entity.getNameForCrossEntityAggregateMetrics()) {
          namesForCrossEntityAggregatesSeen.add(
              entity.getNameForCrossEntityAggregateMetrics());
        }
      }
    }
    return violations;
  }
}
