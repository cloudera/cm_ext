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

import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class NameForCrossEntityAggregatesPrefixedWithServiceNameValidator
    extends AbstractMonitoringValidator<String> {

  private final ImmutableSet<String> builtInNamesForCrossEntityAggregateMetrics;
  private final boolean forServiceNodes;

  public NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor,
      ImmutableSet<String> builtInNamesForCrossEntityAggregateMetrics,
      boolean forServiceNodes) {
    super(serviceDescriptor);
    Preconditions.checkNotNull(builtInNamesForCrossEntityAggregateMetrics);
    this.builtInNamesForCrossEntityAggregateMetrics =
        builtInNamesForCrossEntityAggregateMetrics;
    this.forServiceNodes = forServiceNodes;
  }

  @Override
  public String getDescription() {
    return
        "Validates that the name for cross entity aggregate metrics starts " +
        "with the service name and an underscore, e.g., 'echo_'.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      String nameForCrossEntityAggregateMetrics,
      DescriptorPathImpl path) {
    if (null == nameForCrossEntityAggregateMetrics) {
      // This is valid, we will construct the name ourselves.
      return noViolations();
    }

    if (builtInNamesForCrossEntityAggregateMetrics.contains(
        nameForCrossEntityAggregateMetrics)) {
      return noViolations();
    }
    String serviceName;
    if (forServiceNodes) {
      // The service prefix in the service name for cross-entity aggregate
      // metrics does not need to end with an underscore, e.g., "echos" for the
      // ECHO service.
      serviceName = serviceDescriptor.getName().toLowerCase();
    } else {
      // The service prefix in roles or entities names for cross-entity aggregate
      // metrics must end with an underscore, e.g., "echo_webservers".
      serviceName = serviceDescriptor.getName().toLowerCase() + "_";
    }

    if (!nameForCrossEntityAggregateMetrics.startsWith(serviceName)) {
      String msg = String.format(
          "Name for cross entity aggregate metrics '%s' does not start with " +
          "the service name",
          nameForCrossEntityAggregateMetrics);
      return forViolation(msg,
                          nameForCrossEntityAggregateMetrics,
                          nameForCrossEntityAggregateMetrics,
                          path);
    }
    return noViolations();
  }
}
