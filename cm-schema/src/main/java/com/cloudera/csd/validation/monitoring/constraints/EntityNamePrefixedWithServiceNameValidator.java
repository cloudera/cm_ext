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

import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class EntityNamePrefixedWithServiceNameValidator extends
    AbstractMonitoringValidator<MetricEntityTypeDescriptor> {

  private final ImmutableSet<String> builtInEntityTypes;

  public EntityNamePrefixedWithServiceNameValidator(
      ImmutableSet<String> builtInEntityTypes) {
    Preconditions.checkNotNull(builtInEntityTypes);
    this.builtInEntityTypes = builtInEntityTypes;
  }

  @Override
  public String getDescription() {
    return
        "Validates that the name of the MetricEntityTypeDescriptor starts " +
        "with the service name followed by an underscore ('_'). This ensures " +
        "that entity names are unique across services.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricEntityTypeDescriptor entity,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(path);
    path = constructPathFromProperty(entity, "name", path);
    String entityName = entity.getName();
    String serviceName =
        context.serviceDescriptor.getName().toUpperCase() + "_";
    if (!entityName.startsWith(serviceName) &&
        !builtInEntityTypes.contains(entityName)) {
      String msg = String.format(
          "Entity '%s' does not start with the service name ('%s')",
          entityName,
          serviceName);
      return forViolation(msg, entity, entityName, path);
    }
    return noViolations();
  }
}
