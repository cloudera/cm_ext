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
package com.cloudera.csd.descriptors;

import org.hibernate.validator.constraints.NotEmpty;

import com.cloudera.csd.descriptors.InterfaceStability.Unstable;
import com.cloudera.csd.validation.monitoring.constraints.MetricNameFormat;
import com.cloudera.csd.validation.references.annotations.Named;

/**
 * Describes a metric.
 */
@Named
@Unstable
public interface MetricDescriptor {
  /**
   * Returns the name of the metric. This name uniquely identifies this metric,
   * and is used to reference the metric in the Cloudera Manager API and
   * charting features.
   * @return
   */
  @NotEmpty
  @MetricNameFormat
  String getName();

  /**
   * Returns the display name of the metric.
   * @return
   */
  @NotEmpty
  String getLabel();

  /**
   * Returns the description of the metric.
   * @return
   */
  @NotEmpty
  String getDescription();

  /**
   * Returns the numerator unit for the metric, for example, 'bytes'.
   * @return
   */
  @NotEmpty
  String getNumeratorUnit();

  /**
   * Returns the denominator unit for the metric, for example, 'seconds'.
   * @return
   */
  String getDenominatorUnit();

  /**
   * Returns true if the metric is a counter. Counter-based metrics are treated
   * specially within Cloudera Manager. They are in general converted to rates
   * and exposed as rates.
   * @return
   */
  boolean isCounter();

  /**
   * Returns the name of the "weighting metric", if any, associated with this
   * metric.
   *
   * A "weighting metric" is one metric in a metric pair used to track both the
   * count of something as well as an average of values associated with those
   * counted somethings. For example, one metric might count the number of file
   * upload operations processed while a second metric counts the average time
   * taken by those operations. In this case, the average time would have the
   * operation counter as the weighting metric. The weighting metric is used by
   * Cloudera Manager when aggregating a metric over time or across metric
   * entities.
   *
   * @return
   */
  @MetricNameFormat
  String getWeightingMetricName();

  /**
   * Returns an opaque context for the metric. This context will be available
   * for the metric in the metric schema.
   * @return
   */
  String getContext();
}
