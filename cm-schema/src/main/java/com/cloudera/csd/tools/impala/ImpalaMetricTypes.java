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
package com.cloudera.csd.tools.impala;

import com.google.common.base.Preconditions;

/**
 * A class that defines the different metric types the Impala adapter supports
 * and the rules for generating MetricDescriptors off them.
 */
public class ImpalaMetricTypes {

  /**
   * An enum of all the Impala metric types this adapter supports.
   */
  public static enum ImpalaMetricType {
    GAUGE,
    COUNTER,
    STATISTICAL
  }

  /**
   * The class of the specific metrics the complex Impala metric types expose.
   */
  public static enum ImpalaMetricClass {
    COUNTER,
    GAUGE
  }

  /**
   * An interface defining what statistical metrics expose.
   */
  public interface ComplexImpalaMetric {
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
  }

  /**
   * Supported types of metrics generated from a Codahale Histogram.
   * 'suffix' will be appended to the base metric name to generate each specific
   * metric.
   */
  public enum StatisticalMetricType implements ComplexImpalaMetric {
    COUNT("_count", ImpalaMetricClass.COUNTER, ": Samples"),
    LAST("_last", ImpalaMetricClass.GAUGE, ": Last"),
    MIN("_min", ImpalaMetricClass.GAUGE, ": Min"),
    MAX("_max", ImpalaMetricClass.GAUGE, ": Max"),
    MEAN("_mean", ImpalaMetricClass.GAUGE, ": Mean"),
    STDDEV("_stddev", ImpalaMetricClass.GAUGE, ": Standard Deviation");

    private final String metricNameSuffix;
    private final ImpalaMetricClass metricClass;
    private final String descriptionSuffix;

    private StatisticalMetricType(String val,
                                  ImpalaMetricClass metricClass,
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

    @Override
    public boolean isCounter() {
      return metricClass.equals(ImpalaMetricClass.COUNTER);
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
}
