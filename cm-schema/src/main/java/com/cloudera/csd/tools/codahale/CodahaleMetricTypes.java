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
  public enum CodahaleMetricType {
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
  public enum CodahaleMetricClass {
    COUNTER,
    GAUGE,
    RATE_GAUGE
  }

  /**
   * An interface defining what histogram, timer, and meter codahale metric
   * types expose.
   */
  public interface ComplexCodahaleMetric {
    /**
     * Returns 'true' if the underlying codahale metric type is a counter.
     * @return
     */
    boolean isCounter();

    /**
     * Returns 'true' if the underlying codahale metric type is a rate.
     * @return
     */
    boolean isRate();

    /**
     * Generates the full metric name from the 'metricNameBase' and the specific
     * underlying codahale metric type.
     * @param metricNameBase
     * @return
     */
    String makeMetricName(String metricNameBase);

    /**
     * Return the suffix used in the metric name.
     * @return
     */
    String suffix();
  }

  /**
   * Supported types of metrics generated from a Codahale Histogram.
   * 'suffix' will be appended to the base metric name to generate each specific
   * metric.
   */
  public enum HistogramMetricType implements ComplexCodahaleMetric {
    MAX("_max", CodahaleMetricClass.GAUGE, ": Max"),
    MIN("_min", CodahaleMetricClass.GAUGE, ": Min"),
    MEAN("_avg", CodahaleMetricClass.GAUGE, ": Avg"),
    COUNT("_count", CodahaleMetricClass.COUNTER, ": Samples"),
    STDDEV("_stddev", CodahaleMetricClass.GAUGE, ": Standard Deviation"),
    MEDIAN("_median", CodahaleMetricClass.GAUGE, ": 50th Percentile"),
    PERCENTILE_75("_75th_percentile", CodahaleMetricClass.GAUGE, ": 75th Percentile"),
    PERCENTILE_99("_99th_percentile", CodahaleMetricClass.GAUGE, ": 99th Percentile"),
    PERCENTILE_999("_999th_percentile", CodahaleMetricClass.GAUGE, ": 999th Percentile");

    private final String metricNameSuffix;
    private final CodahaleMetricClass metricClass;
    private final String descriptionSuffix;

    HistogramMetricType(String val,
                        CodahaleMetricClass metricClass,
                        String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(metricClass);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    @Override
    public String suffix() {
      return metricNameSuffix;
    }

    @Override
    public boolean isCounter() {
      return metricClass.equals(CodahaleMetricClass.COUNTER);
    }

    @Override
    public boolean isRate() {
      return false;
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    @Override
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
  public enum TimerMetricType implements ComplexCodahaleMetric {
    MAX("_max", CodahaleMetricClass.GAUGE,  ": Max"),
    MIN("_min", CodahaleMetricClass.GAUGE,  ": Min"),
    MEAN("_avg", CodahaleMetricClass.GAUGE,  ": Avg"),
    COUNT("_count", CodahaleMetricClass.COUNTER, ": Samples"),
    STDDEV("_stddev", CodahaleMetricClass.GAUGE,  ": Standard Deviation"),
    MEDIAN("_median", CodahaleMetricClass.GAUGE,  ": 50th Percentile"),
    PERCENTILE_75("_75th_percentile", CodahaleMetricClass.GAUGE,  ": 75th Percentile"),
    PERCENTILE_99("_99th_percentile", CodahaleMetricClass.GAUGE,  ": 99th Percentile"),
    PERCENTILE_999("_999th_percentile", CodahaleMetricClass.GAUGE,  ": 999th Percentile"),
    ONE_MIN_RATE("_1min_rate", CodahaleMetricClass.RATE_GAUGE,  ": 1 Min Rate"),
    FIVE_MIN_RATE("_5min_rate", CodahaleMetricClass.RATE_GAUGE,  ": 5 Min Rate"),
    FIFTEEN_MIN_RATE("_15min_rate", CodahaleMetricClass.RATE_GAUGE,  ": 15 Min Rate");

    private final String metricNameSuffix;
    private final CodahaleMetricClass metricClass;
    private final String descriptionSuffix;

    TimerMetricType(String val,
                    CodahaleMetricClass metricClass,
                    String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(metricClass);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    @Override
    public String suffix() {
      return metricNameSuffix;
    }

    @Override
    public boolean isCounter() {
      return metricClass.equals(CodahaleMetricClass.COUNTER);
    }

    @Override
    public boolean isRate() {
      return metricClass.equals(CodahaleMetricClass.RATE_GAUGE);
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    @Override
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
  public enum MeterMetricType implements ComplexCodahaleMetric {
    COUNT("_count", CodahaleMetricClass.COUNTER, ""),
    MEAN_RATE_GAUGE("_avg_rate", CodahaleMetricClass.RATE_GAUGE, ": Avg Rate"),
    ONE_MIN_RATE_GAUGE("_1min_rate", CodahaleMetricClass.RATE_GAUGE, ": 1 Min Rate"),
    FIVE_MIN_RATE_GAUGE("_5min_rate", CodahaleMetricClass.RATE_GAUGE, ": 5 Min Rate"),
    FIFTEEN_MIN_RATE_GAUGE("_15min_rate", CodahaleMetricClass.RATE_GAUGE, ": 15 Min Rate");

    private final String metricNameSuffix;
    private final CodahaleMetricClass metricClass;
    private final String descriptionSuffix;

    MeterMetricType(String val,
                    CodahaleMetricClass metricClass,
                    String descriptionSuffix) {
      Preconditions.checkNotNull(val);
      Preconditions.checkNotNull(descriptionSuffix);
      this.metricNameSuffix = val;
      this.metricClass = metricClass;
      this.descriptionSuffix = descriptionSuffix;
    }

    @Override
    public String suffix() {
      return metricNameSuffix;
    }

    @Override
    public boolean isCounter() {
      return CodahaleMetricClass.COUNTER.equals(metricClass);
    }

    @Override
    public boolean isRate() {
      return CodahaleMetricClass.RATE_GAUGE.equals(metricClass);
    }

    public String descriptionSuffix() {
      return descriptionSuffix;
    }

    @Override
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
