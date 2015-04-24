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
package com.cloudera.csd.tools.codahale;

import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.HistogramMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.MeterMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.TimerMetricType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * This class captures metric conventions -- currently limited to metric
 * context construction -- to be used by the adapter to produce CM metrics
 * from codahale metrics. Fundamentally this is a dictionary of suffixes
 * that will be appended to the base metric contexts when appropriate. For
 * gauges and counters a single suffix exists. Meters, timers and histograms
 * have a map of suffixes, keyed on their sub metric types. See the
 * CodahaleMetricsServletMetricConventionsGenerator for an example of a set
 * of conventions built to allow simple collection code when the codahale 2.2.0
 * MetricsServlet is in use.
 */
public class CodahaleMetricConventions {

  // A null value in a convention, including in a the meter, time and histogram
  // map will result in the metric context being returned for the metric as is.
  // That is: it is equivalent to their being no convention in that case.
  @Nullable
  @JsonProperty
  public String gaugeContextSuffix;
  @Nullable
  @JsonProperty
  public String counterContextSuffix;
  @JsonProperty
  public Map<MeterMetricType, String> meterContextSuffixes;
  @JsonProperty
  public Map<TimerMetricType, String> timerContextSuffixes;
  @JsonProperty
  public Map<HistogramMetricType, String> histogramContextSuffixes;

  /**
   * Construct a context for a gauge.
   * @param baseContext
   * @return
   */
  public String makeGaugeContext(String baseContext) {
    Preconditions.checkNotNull(baseContext);
    if (gaugeContextSuffix != null) {
      return baseContext + gaugeContextSuffix;
    } else {
      return baseContext;
    }
  }

  /**
   * Construct a context for a counter.
   * @param baseContext
   * @return
   */
  public String makeCounterContext(String baseContext) {
    Preconditions.checkNotNull(baseContext);
    if (counterContextSuffix != null) {
      return baseContext + counterContextSuffix;
    } else {
      return baseContext;
    }
  }

  /**
   * Construct a context for a meter.
   * @param baseContext
   * @param type
   * @return
   */
  public String makeMeterContext(
      String baseContext,
      MeterMetricType type) {
    Preconditions.checkNotNull(baseContext);
    Preconditions.checkNotNull(type);
    String suffix = meterContextSuffixes.get(type);
    if (suffix != null) {
      return baseContext + suffix;
    } else {
      return baseContext;
    }
  }

  /**
   * Construct a context for a timer.
   * @param baseContext
   * @param type
   * @return
   */
  public String makeTimerContext(
      String baseContext,
      TimerMetricType type) {
    Preconditions.checkNotNull(baseContext);
    Preconditions.checkNotNull(type);
    String suffix = timerContextSuffixes.get(type);
    if (suffix != null) {
      return baseContext + suffix;
    } else {
      return baseContext;
    }
  }

  /**
   * Construct a context for a histogram.
   * @param baseContext
   * @param type
   * @return
   */
  public String makeHistogramContext(
      String baseContext,
      HistogramMetricType type) {
    Preconditions.checkNotNull(baseContext);
    Preconditions.checkNotNull(type);
    String suffix = histogramContextSuffixes.get(type);
    if (suffix != null) {
      return baseContext + suffix;
    } else {
      return baseContext;
    }
  }
}
