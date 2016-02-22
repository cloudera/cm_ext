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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class ParentsAreReachableUsingAttributesValidator extends
    AbstractMonitoringValidator<MetricEntityTypeDescriptor> {

  @Override
  public String getDescription() {
    return
        "Validates that the names of the parents of a MetricEntityType can be " +
        "constructed using the attributes defined for the entity.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricEntityTypeDescriptor entity,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(path);
    if (null == entity.getParentMetricEntityTypeNames()) {
      return noViolations();
    }
    List<ConstraintViolation<T>> ret = Lists.newArrayList();
    path = constructPathFromProperty(entity,
                                     "parentMetricEntityTypeNames",
                                     path);
    for (String parentName : entity.getParentMetricEntityTypeNames()) {
      MetricEntityTypeDescriptor parent =
          context.entitiesDefined.get(parentName);
      if (null == parent || null == parent.getEntityNameFormat()) {
        // We just ignored this. It will be caught by a different validator.
        continue;
      }
      Set<String> attributes = Sets.newHashSet();
      safeAddAllToCollection(attributes, entity.getImmutableAttributeNames());
      safeAddAllToCollection(attributes, entity.getMutableAttributeNames());
      for (String parentNamePart : parent.getEntityNameFormat()) {
        if (!attributes.contains(parentNamePart)) {
          String msg = String.format(
              "Metric entity type '%s' does not have attribute '%s' to be " +
              "able to construct the name of the parent '%s'",
              entity.getName(),
              parentNamePart,
              parent.getName());
          ret.addAll(
              AbstractMonitoringValidator.<T, MetricEntityTypeDescriptor>
                  forViolation(msg, entity, parent.getName(), path));
        }
      }
    }
    return ret;
  }
}
