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
package com.cloudera.csd.descriptors.health;

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.HEALTH;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.PARAMETERS;

import com.cloudera.csd.validation.monitoring.constraints.MetricNameFormat;
import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class EntityMetricHealthTestDescriptor implements HealthTestDescriptor {

  private String name;
  private String label;
  private String description;
  private String metric;
  private String divisorMetric;
  private Long timeWindowSec;
  private CsdComparisonOperator comparisonOperator;
  private CsdAggregationFunction aggregationFunction;
  private String greenMessage;
  private Double yellowThreshold;
  private String yellowMessage;
  private Double redThreshold;
  private String redMessage;
  private HealthTestAdviceDescriptor advice;

  public EntityMetricHealthTestDescriptor() {
  }

  @NotEmpty
  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @NotEmpty
  @Override
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @NotEmpty
  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Name of the counter or gauge metric to test against.
   *
   * @return metric name
   */
  @NotEmpty
  @MetricNameFormat
  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  /**
   * Optional metric to be used to divide the main metric, i.e. the denominator.
   * Useful for calculating ratio, e.g. free disk space ratio.
   *
   * @return Name of the counter or gauge metric to divide with
   */
  @MetricNameFormat
  public String getDivisorMetric() {
    return divisorMetric;
  }

  public void setDivisorMetric(String divisorMetric) {
    this.divisorMetric = divisorMetric;
  }

  /**
   * The time window to look back from the time of health test. The health test
   * is evaluated against the statuses within the window. Depending on the
   * frequency of health checks, and the resulting status, in general a window
   * that contain twice the maximum threshold number of statuses is recommended.
   * For example, if the largest threshold is 3, and status is expected to
   * be reported every 5 minutes, then a window of at least 30 minutes is recommended.
   *
   * If there is less status than the maximum threshold within the time window,
   * health will be red.
   *
   * @return time window in seconds
   */
  @NotNull
  @DecimalMin("1")
  @DecimalMax("3600")
  public Long getTimeWindowSec() {
    return timeWindowSec;
  }

  public void setTimeWindowSec(Long timeWindowSec) {
    this.timeWindowSec = timeWindowSec;
  }

  /**
   * Type of comparison to perform between the actual aggregated metric value
   * and the threshold.
   *
   * @return the comparison operator to use
   */
  @NotNull
  public CsdComparisonOperator getComparisonOperator() {
    return comparisonOperator;
  }

  public void setComparisonOperator(CsdComparisonOperator operator) {
    this.comparisonOperator = operator;
  }

  /**
   * The function used to aggregate all data points over time into one value.
   *
   * @return aggregation function
   */
  @NotNull
  public CsdAggregationFunction getAggregationFunction() {
    return aggregationFunction;
  }

  public void setAggregationFunction(CsdAggregationFunction timeAggregation) {
    this.aggregationFunction = timeAggregation;
  }

  /**
   * Threshold that when met, applying {@link #getComparisonOperator()} to
   * actual metric value and the threshold, the health would be yellow.
   *
   * @return threshold to consider health yellow
   */
  public Double getYellowThreshold() {
    return yellowThreshold;
  }

  public void setYellowThreshold(Double yellowThreshold) {
    this.yellowThreshold = yellowThreshold;
  }

  /**
   * Threshold that when met, applying {@link #getComparisonOperator()} to
   * actual metric value and the threshold, the health would be red.
   *
   * @return threshold to consider health red
   */
  public Double getRedThreshold() {
    return redThreshold;
  }

  public void setRedThreshold(Double redThreshold) {
    this.redThreshold = redThreshold;
  }

  /**
   * The health message to display when green. The following variables are provided:
   * <ul>
   * <li>metric.value = Actual aggregated metric value over the time window.
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  @NotEmpty
  @AvailableSubstitutions(type={PARAMETERS, HEALTH})
  public String getGreenMessage() {
    return greenMessage;
  }

  public void setGreenMessage(String greenMessage) {
    this.greenMessage = greenMessage;
  }

  /**
   * The health message to display when yellow. The following variables are provided:
   * <ul>
   * <li>metric.value = Actual aggregated metric value over the time window.
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  @AvailableSubstitutions(type={PARAMETERS, HEALTH})
  public String getYellowMessage() {
    return yellowMessage;
  }

  public void setYellowMessage(String yellowMessage) {
    this.yellowMessage = yellowMessage;
  }

  /**
   * The health message to display when red. The following variables are provided:
   * <ul>
   * <li>metric.value = Actual aggregated metric value over the time window.
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  @AvailableSubstitutions(type={PARAMETERS, HEALTH})
  public String getRedMessage() {
    return redMessage;
  }

  public void setRedMessage(String redMessage) {
    this.redMessage = redMessage;
  }

  @Override
  public HealthTestAdviceDescriptor getAdvice() {
    return advice;
  }

  public void setAdvice(HealthTestAdviceDescriptor advice) {
    this.advice = advice;
  }
}
