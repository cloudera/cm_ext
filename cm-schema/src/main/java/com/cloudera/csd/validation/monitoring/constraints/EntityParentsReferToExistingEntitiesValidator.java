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
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class EntityParentsReferToExistingEntitiesValidator extends
    AbstractMonitoringValidator<MetricEntityTypeDescriptor> {

  private final ImmutableSet<String> builtInRoleTypes;
  private final ImmutableSet<String> builtInEntityTypes;

  public EntityParentsReferToExistingEntitiesValidator(
      ImmutableSet<String> builtInRoleTypes,
      ImmutableSet<String> builtInEntityTypes) {
    this.builtInRoleTypes = Preconditions.checkNotNull(builtInRoleTypes);
    this.builtInEntityTypes = Preconditions.checkNotNull(builtInEntityTypes);
  }

  @Override
  public String getDescription() {
    return
        "Validates that the parents defined for an entity refer to other types " +
        "defined in this descriptor: another entity, a role, or the service.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricEntityTypeDescriptor entity,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(path);
    if (null == entity.getParentMetricEntityTypeNames() ||
        entity.getParentMetricEntityTypeNames().isEmpty()) {
      return noViolations();
    }
    Set<String> entitiesNames =
        Sets.newHashSet(context.entitiesDefined.keySet());
    entitiesNames.add(
        context.serviceDescriptor.getName().toUpperCase());
    entitiesNames.addAll(context.rolesDefined.keySet());
    path = constructPathFromProperty(entity,
                                     "parentMetricEntityTypeNames",
                                     path);
    List<ConstraintViolation<T>> ret = Lists.newArrayList();
    for (String parentName : entity.getParentMetricEntityTypeNames()) {
      if (!entitiesNames.contains(parentName) &&
          !builtInRoleTypes.contains(parentName) &&
          !builtInEntityTypes.contains(parentName)) {
        String msg = String.format(
            "Unknown parent '%s' for metric entity type '%s'.",
            parentName,
            entity.getName());
        ret.addAll(
            AbstractMonitoringValidator.<T, MetricEntityTypeDescriptor>
                forViolation(msg, entity, parentName, path));
      }
    }
    return ret;
  }
}
