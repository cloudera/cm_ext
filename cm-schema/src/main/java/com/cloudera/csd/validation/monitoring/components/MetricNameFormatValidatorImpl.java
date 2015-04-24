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
package com.cloudera.csd.validation.monitoring.components;

import com.cloudera.csd.validation.monitoring.MonitoringConventions;
import com.cloudera.csd.validation.monitoring.constraints.MetricNameFormat;
import com.cloudera.csd.validation.monitoring.constraints.MetricNameFormatValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a metric name conforms to the format rules. The format rules
 * are syntactic. More validations on the name is done using additional context
 * (e.g., if the metric is a counter or not) in additional validators.
 */
public class MetricNameFormatValidatorImpl implements MetricNameFormatValidator {

  @Override
  public void initialize(MetricNameFormat constraintAnnotation) {
  }

  @Override
  public boolean isValid(String metricName,
                         ConstraintValidatorContext context) {
    if (null == metricName || metricName.isEmpty()) {
      // This is verified by the @NotEmpty. For the purpose of this validator
      // a metric is valid if it's empty or null.
      return true;
    }
    return MonitoringConventions.isValidMetricNameFormat(metricName);
  }

}
