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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class EntityStatusHealthTestDescriptor implements HealthTestDescriptor {

  private String name;
  private String label;
  private String description;
  private String status;
  private Long timeWindowSec;
  private String greenMessage;
  private Integer warningThreshold;
  private String yellowMessage;
  private Integer failureThreshold;
  private String redMessage;
  private String unavailableMessage;
  private HealthTestAdviceDescriptor advice;

  public EntityStatusHealthTestDescriptor() {
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

  public void setLabel(String name) {
    this.label = name;
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
   * Name of the status metric to test against. Must be a status type of metric.
   *
   * @return metric name
   */
  @NotEmpty
  @MetricNameFormat
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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
  @Min(1)
  @Max(3600)
  public Long getTimeWindowSec() {
    return timeWindowSec;
  }

  public void setTimeWindowSec(Long timeWindowSec) {
    this.timeWindowSec = timeWindowSec;
  }

  /**
   * Number of warning or failure within the window to consider health yellow,
   * i.e. yellow if actual number is greater or equal to this threshold.
   *
   * @return required number of warning
   */
  @NotNull
  @Min(1)
  @Max(10)
  public Integer getWarningThreshold() {
    return warningThreshold;
  }

  public void setWarningThreshold(Integer warningThreshold) {
    this.warningThreshold = warningThreshold;
  }

  /**
   * Number of failure within the window to consider health red, i.e. red if
   * actual number is greater or equal to this threshold.
   *
   * @return required number of failure
   */
  @NotNull
  @Min(1)
  @Max(10)
  public Integer getFailureThreshold() {
    return failureThreshold;
  }

  public void setFailureThreshold(Integer failureThreshold) {
    this.failureThreshold = failureThreshold;
  }

  /**
   * The health message to display when green. The following variables are provided:
   * <ul>
   * <li>status.count = Number of OK status within the time window.
   * <li>status.message = Message of the last status, regardless of type
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
   * <li>status.count = Number of WARNING or FAILURE status within the time window.
   * <li>status.threshold = Threshold for number of WARNING or FAILURE
   * <li>status.message = Message of the last WARNING or FAILURE status
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  @NotEmpty
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
   * <li>status.count = Number of FAILURE status within the time window.
   * <li>status.threshold = Threshold for number of FAILURE
   * <li>status.message = Message of the last FAILURE status
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  @NotEmpty
  @AvailableSubstitutions(type={PARAMETERS, HEALTH})
  public String getRedMessage() {
    return redMessage;
  }

  public void setRedMessage(String redMessage) {
    this.redMessage = redMessage;
  }

  /**
   * Optional health message to display when the test is unavailable. The
   * following variables are provided:
   * <ul>
   * <li>status.count = Number of UNKNOWN status within the time window.
   * <li>status.message = Message of the last UNKNOWN status
   * </ul>
   * Other than these, parameters for the subject of the health test is also provided.
   *
   * @return the message
   */
  public String getUnavailableMessage() {
    return unavailableMessage;
  }

  public void setUnavailableMessage(String unavailableMessage) {
    this.unavailableMessage = unavailableMessage;
  }

  @Override
  public HealthTestAdviceDescriptor getAdvice() {
    return advice;
  }

  public void setAdvice(HealthTestAdviceDescriptor advice) {
    this.advice = advice;
  }
}
