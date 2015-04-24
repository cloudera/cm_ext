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
 * see getDocumentation for details.
 */
public class DenominatorValidator
    extends AbstractMonitoringValidator<MetricDescriptor> {

  public DenominatorValidator(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
    super(serviceDescriptor);
  }

  @Override
  public String getDescription() {
    return
        "Validates that the metric has a valid denominator. Metrics with " +
        "names ending with " + MonitoringConventions.RATE_SUFFIX + " are " +
        "assumed to be specifying a rate and need to have a denominator. " +
        "Counter metrics should never have denominators.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MetricDescriptor metricDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(metricDescriptor);
    Preconditions.checkNotNull(path);
    path = constructPathFromProperty(metricDescriptor, "name", path);
    String metricName = metricDescriptor.getName();
    String denominatorUnit = metricDescriptor.getDenominatorUnit();
    if (!MonitoringConventions.isValidDenominatorForMetricWithRateEnding(
        metricName,
        denominatorUnit)) {
      String msg = String.format(
        "Non-counter metric '%s' ends with " +
        MonitoringConventions.RATE_SUFFIX + " but has no denominator",
        metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }

    if (metricDescriptor.isCounter() &&
        !MonitoringConventions.isValidDenominatorForCounterMetric(
        denominatorUnit)) {
      String msg = String.format(
        "Counter metric '%s' has a denominator and should not",
        metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, metricName, path);
    }
    return noViolations();
  }

}
