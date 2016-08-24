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

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;

/**
 * see getDescription for more details.
 */
public class AttributesReferToExistingAttributesValidator
    extends AbstractMonitoringValidator<MetricEntityTypeDescriptor> {

  private final ImmutableSet<String> builtInAttributes;

  public AttributesReferToExistingAttributesValidator(
      Set<String> builtInAttributes) {
    Preconditions.checkNotNull(builtInAttributes);
    this.builtInAttributes = ImmutableSet.copyOf(builtInAttributes);
  }

  @Override
  public String getDescription() {
    return
        "Validates that immutable and mutable attribute names refer to either " +
        "attributes defined in this descriptor or built-in attribute names.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricEntityTypeDescriptor entity,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(path);

    List<ConstraintViolation<T>> ret = Lists.newArrayList();
    ret.addAll(this.<T>checkAttributes(context,
                                       entity.getImmutableAttributeNames(),
                                       "immutableAttributeNames",
                                       entity,
                                       path));
    ret.addAll(this.<T>checkAttributes(context,
                                       entity.getMutableAttributeNames(),
                                       "mutableAttributeNames",
                                       entity,
                                       path));
    return ret;
  }

  private <T> List<ConstraintViolation<T>> checkAttributes(
      MonitoringValidationContext context,
      @Nullable List<String>attributes,
      String attributesSourceName,
      MetricEntityTypeDescriptor entity,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(attributesSourceName);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(path);
    if (null == attributes) {
      return noViolations();
    }
    List<ConstraintViolation<T>> ret = Lists.newArrayList();
    path = constructPathFromProperty(entity,
                                     attributesSourceName,
                                     path);
    for (String attribute : attributes) {
      if (!context.attributesDefined.containsKey(attribute) &&
          !builtInAttributes.contains(attribute)) {
        String msg = String.format(
            "Unknown attribute '%s' for metric entity type '%s'.",
             attribute,
             entity.getName());
        ret.addAll(
            AbstractMonitoringValidator.<T, MetricEntityTypeDescriptor>
                forViolation(msg, entity, attribute, path));
      }
    }
    return ret;
  }
}
