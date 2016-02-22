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
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for details.
 */
public class AttributeNamePrefixedWithServiceNameValidator extends
    AbstractMonitoringValidator<MetricEntityAttributeDescriptor> {

  private final ImmutableSet<String> builtInAttributes;

  public AttributeNamePrefixedWithServiceNameValidator(
      Set<String> builtInAttributes) {
    Preconditions.checkNotNull(builtInAttributes);
    this.builtInAttributes = ImmutableSet.copyOf(builtInAttributes);
  }

  @Override
  public String getDescription() {
    return
        "Validates that the name of the MetricEntityAttributeDescriptor " +
        "starts with the service name (in small caps). This ensures that " +
        "attributes names are unique across services.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricEntityAttributeDescriptor attribute,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(attribute);
    Preconditions.checkNotNull(path);
    path = constructPathFromProperty(attribute, "name", path);
    String attributeName = attribute.getName();
    String serviceName = context.serviceDescriptor.getName().toLowerCase();
    if (!attributeName.startsWith(serviceName) &&
        !builtInAttributes.contains(attributeName)) {
      String msg = String.format(
          "Attribute '%s' does not start with the service name ('%s')",
          attributeName,
          serviceName);
      return forViolation(msg, attribute, attributeName, path);
    }
    return noViolations();
  }
}
