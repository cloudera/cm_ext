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
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringConventions;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class CounterMetricNameValidator
    extends AbstractMonitoringValidator<MetricDescriptor> {

  public CounterMetricNameValidator(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
    super(serviceDescriptor);
  }

  @Override
  public String getDescription() {
    return
        "Validates that counter metrics conform to certain naming convention. " +
        "Cloudera Manager exposes counters as rates and converts the original " +
        "metric name, foo_bar or foo_bar_count, to foo_bar_rate. This " +
        "validator makes sure that the transformation is possible with given " +
        "metric name.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MetricDescriptor metricDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(metricDescriptor);
    if (!metricDescriptor.isCounter()) {
      return noViolations();
    }
    path = constructPathFromProperty(metricDescriptor, "name", path);
    String metricName = metricDescriptor.getName();
    if (!MonitoringConventions.isValidEndingForCounterMetric(metricName)) {
       String msg = String.format(
          "Counter metric '%s' ends with " + MonitoringConventions.RATE_SUFFIX,
          metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }

    String userVisibleName =
        MonitoringConventions.convertCounterNameToUserFacingReadName(
            true,
            metricName);
    if (!MonitoringConventions.isValidMetricNameFormat(userVisibleName)) {
       String msg = String.format(
          "User visible name %s for counter metric '%s' does not a valid format",
          userVisibleName,
          metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }

    if (metricsDefined.containsKey(userVisibleName)) {
       String msg = String.format(
          "User visible name %s for counter metric %s collides with a metric " +
          "defined in the MDL with the same name",
          userVisibleName,
          metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }
    return noViolations();
  }

}
