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

import com.google.common.base.Preconditions;

/**
 * A class that defines the different metric types the Codahale adapter supports
 * and the rules for generating MetricDescriptors off them.
 */
public class CodahaleMetricTypes {

  /**
   * An enum of all the Codahale metric types this adapter supports.
   */
  public static enum CodahaleMetricType {
    GAUGE,
    COUNTER,
    HISTOGRAM,
    TIMER,
    METER
  }

  /**
   * The class of the specific metrics the complex codahale metric types expose.
   * For example, a timer exposes a few gauge metrics, a counter, and a few
   * rate gauges.
   */
  public static enum MetricClass {
    COUNTER,
    GAUGE,
    RATE_GAUGE
  }

  /**
   * Supported types of metrics generated from a Codahale Histogram.
   * 'suffix' will be appended to the base metric name to generate each specific
   * metric.
   */
  public enum HistogramMetricType {
    MAX("_max", MetricClass.GAUGE, ": Max"),
    MIN("_min", MetricClass.GAUGE, ": Min"),
    MEAN("_avg", MetricClass.GAUGE, ": Avg"),
    COUNT("_count", MetricClass.COUNTER, ": Samples"),
    STDDEV("_stddev", MetricClass.GAUGE, ": Standard Deviation"),
    MEDIAN("_median", MetricClass.GAUGE, ": 50th Percentile"),
    PERCENTILE_75("_75th_percentile", MetricClass.GAUGE, ": 75th Percentile"),
    PERCENTILE_99("_99th_percentile", MetricClass.GAUGE, ": 99th Percentile"),
    PERCENTILE_999("_999th_percentile", MetricClass.GAUGE, ": 999th Percentile");

    private final String metricNameSuffix;
    private final MetricClass metricClass;
    private final String descriptionSuffix;

    private HistogramMetricType(String val,
                                MetricClass metricClass,
                                String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(metricClass);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    public String suffix() {
      return metricNameSuffix;
    }

    public boolean isCounter() {
      return metricClass.equals(MetricClass.COUNTER);
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    public String makeMetricName(String metricName) {
      Preconditions.checkNotNull(metricName);
      return String.format("%s%s", metricName, suffix());
    }

    public String makeMetricLabel(String baseLabel) {
      return makeMetricDescription(baseLabel);
    }

    public String makeMetricDescription(String baseDescription) {
      Preconditions.checkNotNull(baseDescription);
      return String.format("%s%s", baseDescription, descriptionSuffix());
    }
  }

  /**
   * Supported types of metrics generated from a Codahale Timer. 'suffix' will
   * be appended to the base metric name to generate each specific metric.
   */
  public enum TimerMetricType {
    MAX("_max", MetricClass.GAUGE,  ": Max"),
    MIN("_min", MetricClass.GAUGE,  ": Min"),
    MEAN("_avg", MetricClass.GAUGE,  ": Avg"),
    COUNT("_count", MetricClass.COUNTER, ": Samples"),
    STDDEV("_stddev", MetricClass.GAUGE,  ": Standard Deviation"),
    MEDIAN("_median", MetricClass.GAUGE,  ": 50th Percentile"),
    PERCENTILE_75("_75th_percentile", MetricClass.GAUGE,  ": 75th Percentile"),
    PERCENTILE_99("_99th_percentile", MetricClass.GAUGE,  ": 99th Percentile"),
    PERCENTILE_999("_999th_percentile", MetricClass.GAUGE,  ": 999th Percentile"),
    ONE_MIN_RATE("_1min_rate", MetricClass.RATE_GAUGE,  ": 1 Min Rate"),
    FIVE_MIN_RATE("_5min_rate", MetricClass.RATE_GAUGE,  ": 5 Min Rate"),
    FIFTEEN_MIN_RATE("_15min_rate", MetricClass.RATE_GAUGE,  ": 15 Min Rate");

    private final String metricNameSuffix;
    private final MetricClass metricClass;
    private final String descriptionSuffix;

    private TimerMetricType(String val,
                            MetricClass metricClass,
                            String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(metricClass);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    public String suffix() {
      return metricNameSuffix;
    }

    public boolean isCounter() {
      return metricClass.equals(MetricClass.COUNTER);
    }

    public boolean isRate() {
      return metricClass.equals(MetricClass.RATE_GAUGE);
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    public String makeMetricName(String metricName) {
      Preconditions.checkNotNull(metricName);
      return String.format("%s%s", metricName, suffix());
    }

    public String makeMetricLabel(String baseLabel) {
      return makeMetricDescription(baseLabel);
    }

    public String makeMetricDescription(String metricName) {
      Preconditions.checkNotNull(metricName);
      return String.format("%s%s", metricName, descriptionSuffix());
    }
  }

  /**
   * Supported types of metrics generated from a Codahale Meter. 'suffix' will
   * be append to the base metric name to generate each specific metric.
   */
  public enum MeterMetricType {
    COUNT("_count", MetricClass.COUNTER, ""),
    MEAN_RATE_GAUGE("_avg_rate", MetricClass.RATE_GAUGE, ": Avg Rate"),
    ONE_MIN_RATE_GAUGE("_1min_rate", MetricClass.RATE_GAUGE, ": 1 Min Rate"),
    FIVE_MIN_RATE_GAUGE("_5min_rate", MetricClass.RATE_GAUGE, ": 5 Min Rate"),
    FIFTEEN_MIN_RATE_GAUGE("_15min_rate", MetricClass.RATE_GAUGE, ": 15 Min Rate");

    private final String metricNameSuffix;
    private final MetricClass metricClass;
    private final String descriptionSuffix;

    private MeterMetricType(String val,
                            MetricClass metricClass,
                            String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    public String suffix() {
      return metricNameSuffix;
    }

    public boolean isCounter() {
      return MetricClass.COUNTER.equals(metricClass);
    }

    public boolean isRate() {
      return MetricClass.RATE_GAUGE.equals(metricClass);
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    public String makeMetricName(String metricName) {
      Preconditions.checkNotNull(metricName);
      return String.format("%s%s", metricName, suffix());
    }

    public String makeMetricLabel(String baseLabel) {
      return makeMetricDescription(baseLabel);
    }

    public String makeMetricDescription(String metricName) {
      Preconditions.checkNotNull(metricName);
      return String.format("%s%s", metricName, descriptionSuffix());
    }
  }
}
