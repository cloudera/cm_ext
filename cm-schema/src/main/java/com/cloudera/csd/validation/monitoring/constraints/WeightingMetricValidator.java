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
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * See getDescription for more details.
 */
public class WeightingMetricValidator
    extends AbstractMonitoringValidator<MetricDescriptor> {

  public WeightingMetricValidator(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
    super(serviceDescriptor);
  }

  @Override
  public String getDescription() {
    return
        "Validates that the weighting metric name refers to a metric defined " +
        "in the MDL.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MetricDescriptor metricDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(metricDescriptor);
    Preconditions.checkNotNull(path);
    path = constructPathFromProperty(metricDescriptor, "name", path);
    String weightingMetric = metricDescriptor.getWeightingMetricName();
    if (null == weightingMetric || weightingMetric.isEmpty()) {
      return noViolations();
    }
    if (!metricsDefined.containsKey(weightingMetric)) {
      String msg = String.format(
        "Weighting metric '%s' for metric '%s' refers to unknown metric. ",
        metricDescriptor.getWeightingMetricName(),
        metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, weightingMetric, path);
    }
    if (weightingMetric.equals(metricDescriptor.getName())) {
      String msg = String.format(
        "Weighting metric for metric '%s' refers to itself. ",
        metricDescriptor.getName());
      return forViolation(msg, metricDescriptor, weightingMetric, path);
    }
    return noViolations();
  }
}
