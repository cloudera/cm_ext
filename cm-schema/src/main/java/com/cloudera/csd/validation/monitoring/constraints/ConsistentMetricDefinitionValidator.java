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

import org.apache.commons.lang.StringUtils;

/**
 * See getDescription for more details.
 */
public class ConsistentMetricDefinitionValidator
    extends AbstractMonitoringValidator<MetricDescriptor> {

  @Override
  public String getDescription() {
    return
        "Validates that metric definitions are consistent if that metric is " +
        "defined for multiple entity types.";
  }

  @Override
  public <T> List<ConstraintViolation<T>> validate(
      MonitoringValidationContext context,
      MetricDescriptor metricDescriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(metricDescriptor);
    Preconditions.checkNotNull(path);

    MetricDescriptor definition =
        context.metricsDefined.get(metricDescriptor.getName());
    Preconditions.checkNotNull(definition);

    if (!StringUtils.equals(
        metricDescriptor.getLabel(),
        definition.getLabel())) {
      String msg = String.format(
          "Inconsistent labels for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.getLabel(),
          definition.getLabel());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.getLabel(), path);
    }

    if (!StringUtils.equals(
        metricDescriptor.getDescription(),
        definition.getDescription())) {
      String msg = String.format(
          "Inconsistent descriptions for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.getDescription(),
          definition.getDescription());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.getDescription(), path);
    }

    if (!StringUtils.equals(
        metricDescriptor.getNumeratorUnit(),
        definition.getNumeratorUnit())) {
      String msg = String.format(
          "Inconsistent numerator units for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.getNumeratorUnit(),
          definition.getNumeratorUnit());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.getNumeratorUnit(), path);
    }

    if (!StringUtils.equals(
        metricDescriptor.getDenominatorUnit(),
        definition.getDenominatorUnit())) {
      String msg = String.format(
          "Inconsistent denominator units for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.getDenominatorUnit(),
          definition.getDenominatorUnit());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.getDenominatorUnit(), path);
    }

    if (metricDescriptor.isCounter() != definition.isCounter()) {
      String msg = String.format(
          "Inconsistent counter definitions for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.isCounter(),
          definition.isCounter());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.isCounter(), path);
    }

    if (!StringUtils.equals(
        metricDescriptor.getWeightingMetricName(),
        definition.getWeightingMetricName())) {
      String msg = String.format(
          "Inconsistent weighting metric names for metric '%s': '%s' and '%s'. ",
          metricDescriptor.getName(),
          metricDescriptor.getWeightingMetricName(),
          definition.getWeightingMetricName());
        return forViolation(
            msg, metricDescriptor, metricDescriptor.getWeightingMetricName(), path);
    }

    return noViolations();
  }
}
