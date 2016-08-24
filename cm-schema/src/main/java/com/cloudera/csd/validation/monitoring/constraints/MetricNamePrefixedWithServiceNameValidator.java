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
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.google.common.base.Preconditions;

import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for details.
 */
public class MetricNamePrefixedWithServiceNameValidator extends
    AbstractMonitoringValidator<MetricDescriptor> {

  @Override
  public String getDescription() {
    return
        "Validates that the name of the MetricDescriptor starts with the " +
        "service name followed by an underscore ('_'). This ensures that " +
        "metric names are unique across services. The validator assumes that " +
        "metric names conforms with the metric name format, e.g., the " +
        "names are in all small caps.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricDescriptor metricDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(metricDescriptor);
    Preconditions.checkNotNull(path);
    path = constructPathFromProperty(metricDescriptor, "name", path);
    String metricName = metricDescriptor.getName();
    String serviceName =
        context.serviceDescriptor.getName().toLowerCase() + "_";
    if (!metricName.startsWith(serviceName)) {
      String msg = String.format(
          "Metric '%s' does not start with the service name",
          metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }
    return noViolations();
  }
}
