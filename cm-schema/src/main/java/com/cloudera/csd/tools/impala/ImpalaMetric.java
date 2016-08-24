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

import com.cloudera.csd.tools.AbstractMetricDefinition;
import com.cloudera.csd.tools.impala.ImpalaMetricTypes.ImpalaMetricType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * A helper class defining the metadata for a single Impala metric.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpalaMetric extends AbstractMetricDefinition {

  public static class Builder extends
    AbstractMetricDefinition.Builder<ImpalaMetric.Builder> {

    private ImpalaMetricType impalaMetricType;
    private String numeratorForCounterMetric;
    private String metricNameForCounterMetric;

    public ImpalaMetric.Builder setImpalaMetricType(
        ImpalaMetricType impalaMetricType) {
      Preconditions.checkNotNull(impalaMetricType);
      this.impalaMetricType = impalaMetricType;
      return this;
    }

    public ImpalaMetric.Builder setNumeratorForCounterMetric(
        String numeratorForCounterMetric) {
      this.numeratorForCounterMetric = numeratorForCounterMetric;
      return this;
    }

    public ImpalaMetric.Builder setMetricNameForCounterMetric(
        String metricNameForCounterMetric) {
      this.metricNameForCounterMetric = metricNameForCounterMetric;
      return this;
    }

    public ImpalaMetric build() {
      return new ImpalaMetric(name,
                              label,
                              description,
                              impalaMetricType,
                              numerator,
                              denominator,
                              numeratorForCounterMetric,
                              metricNameForCounterMetric,
                              context);
    }
  }

  // The type of the metric in the Impala metric system. Certain types are
  // transformed to multiple CM metrics.
  @JsonIgnore
  private ImpalaMetricType metricType;

  // A string to be used for the numerator display name for the counter metric
  // that is part of an Impala statistical metric.
  @JsonIgnore
  private String numeratorForCounterMetric;

  // The name for the counter metric of an Impala statistical metric.
  @JsonIgnore
  private String metricNameForCounterMetric;

  @JsonCreator
  private ImpalaMetric() {
  }

  private ImpalaMetric(String name,
                       String label,
                       String description,
                       ImpalaMetricType metricType,
                       String numerator,
                       String denominator,
                       String counterNumeratorOverride,
                       @Nullable String metricNameForCounterMetric,
                       String context) {
    super(name,
          label,
          description,
          numerator,
          denominator,
          context);
    Preconditions.checkNotNull(metricType);
    if (ImpalaMetricType.STATISTICAL.equals(metricType)) {
      Preconditions.checkNotNull(counterNumeratorOverride);
    }
    this.metricType = metricType;
    this.numeratorForCounterMetric = counterNumeratorOverride;
    this.metricNameForCounterMetric = metricNameForCounterMetric;
  }

  @JsonProperty
  public String getMetricType() {
    return metricType.name();
  }

  @JsonProperty
  public void setMetricType(String metricType) {
    if (null == metricType) {
      return;
    }
    try {
      this.metricType = ImpalaMetricType.valueOf(metricType.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid metric type " + metricType);
    }
  }

  @JsonIgnore
  public ImpalaMetricType getType() {
    return metricType;
  }

  @JsonProperty
  public String getNumeratorForCounterMetric() {
    return numeratorForCounterMetric;
  }

  @JsonProperty
  public void setNumeratorForCounterMetric(String numeratorForCounterMetric) {
    this.numeratorForCounterMetric = numeratorForCounterMetric;
  }

  @JsonProperty
  public String getMetricNameForCounterMetric() {
    return metricNameForCounterMetric;
  }

  @JsonProperty
  public void setMetricNameForCounterMetric(
      @Nullable String metricNameForCounterMetric) {
    this.metricNameForCounterMetric = metricNameForCounterMetric;
  }
}