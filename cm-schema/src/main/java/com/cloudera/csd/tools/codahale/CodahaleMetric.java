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

import com.cloudera.csd.tools.AbstractMetricDefinition;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.CodahaleMetricType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * A helper class defining the metadata for a single codahale metric. Codahale
 * metrics are different than CSD metric descriptors as one codahale metric
 * is usually exposed as more than one CSD metric.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodahaleMetric extends AbstractMetricDefinition {

  public static class Builder extends
    AbstractMetricDefinition.Builder<CodahaleMetric.Builder> {

    private CodahaleMetricType codahaleMetricType;
    private String numeratorForCounterMetric;
    private String metricNameForCounterMetric;
    private String denominatorForRateMetrics;
    private boolean treatGaugeAsCounter;
    private boolean treatCounterAsAGauge;

    public Builder() {
      super();
    }

    public Builder(CodahaleMetric from) {
              Preconditions.checkNotNull(from);
      this.name = from.getName();
      this.label = from.getLabel();
      this.description = from.getDescription();
      this.codahaleMetricType = from.getType();
      this.numerator = from.getNumeratorUnit();
      this.denominator = from.getDenominatorUnit();
      this.numeratorForCounterMetric = from.getNumeratorForCounterMetric();
      this.metricNameForCounterMetric = from.getMetricNameForCounterMetric();
      this.context = from.getContext();
      this.denominatorForRateMetrics = from.getDenominatorForRateMetrics();
    }

    public CodahaleMetric.Builder setCodahaleMetricType(
        CodahaleMetricType codahaleMetricType) {
      Preconditions.checkNotNull(codahaleMetricType);
      this.codahaleMetricType = codahaleMetricType;
      return this;
    }

    public CodahaleMetric.Builder setNumeratorForCounterMetric(
        String numeratorForCounterMetric) {
      this.numeratorForCounterMetric = numeratorForCounterMetric;
      return this;
    }

    public CodahaleMetric.Builder setMetricNameForCounterMetric(
        String metricNameForCounterMetric) {
      this.metricNameForCounterMetric = metricNameForCounterMetric;
      return this;
    }

    public CodahaleMetric.Builder setDenominatorForRateMetrics(
        String denominatorForRateMetrics) {
      this.denominatorForRateMetrics = denominatorForRateMetrics;
      return this;
    }

    public CodahaleMetric.Builder setTreatGaugeAsCounter(
        boolean treatGaugeAsCounter) {
      this.treatGaugeAsCounter = treatGaugeAsCounter;
      return this;
    }

    public CodahaleMetric.Builder setTreatCounterAsAGauge(
        boolean treatCounterAsAGauge) {
      this.treatCounterAsAGauge = treatCounterAsAGauge;
      return this;
    }

    public CodahaleMetric build() {
      return new CodahaleMetric(name,
                                label,
                                description,
                                codahaleMetricType,
                                numerator,
                                denominator,
                                numeratorForCounterMetric,
                                metricNameForCounterMetric,
                                context,
                                denominatorForRateMetrics,
                                treatGaugeAsCounter,
                                treatCounterAsAGauge);
    }
  }


  // The type of the metric in the codahale metric system. Certain types are
  // transformed to multiple cm metrics.
  @JsonIgnore
  private CodahaleMetricType metricType;

  // A string to be used for the numerator display name for the counter metric
  // that is part of a codahale histogram or timer. For example for a
  // histogram that tracks the time it takes for processing an rpc call the
  // numerator can be "milliseconds". The counter unit (i.e, the number of
  // time the histogram update method was called) in this case can be "calls".
  // For a histogram or timer a value must be provided. This is ignored for
  // any other codahale metric types.
  @JsonIgnore
  private String numeratorForCounterMetric;

  // The name for the counter metric of a histogram, timer, or a meter to use
  // instead of the default counter metric name the CodahaleMetricAdapater
  // generates. For example, the default user visible metric name for a
  // codahale histogram counter named get_version_rpc_duration will be
  // get_version_rpc_duration_rate. A better name may be get_version_rpc_calls
  // which will become get_version_rpc_calls_rate.
  @JsonIgnore
  private String metricNameForCounterMetric;

  // The string used for the denominator display name for rate metrics that
  // are part of a timer metrics. Timer metrics must include a non-null
  // value. This value is ignored for all other codahale metric types.
  @JsonIgnore
  private String denominatorForRateMetrics;

  // Some codahale gauges are used as convenient wrappers around counters (e.g.,
  // to make a function call to extract the counter value). This indicates that
  // these gauge metrics should be marked as counters in their corresponding
  // MetricDescriptor. Ignored for all metric types except gauges.
  @JsonIgnore
  private boolean treatGaugeAsACounter;

  // Some codahale counters are used as convenient store for gauges (e.g.,
  // instead of having an atomic long, incrementing and decrementing it and
  // wrapping a gauge around it, using a counter do expose a metric that is
  // essentially a gauge). This indicates that this counter metric should be
  // marked as a gauge in its corresponding MetricDescriptor. Ignored for all
  // metric types except counters.
  @JsonIgnore
  private boolean treatCounterAsAGauge;

  @JsonCreator
  private CodahaleMetric() {
  }

  private CodahaleMetric(String name,
                         String label,
                         String description,
                         CodahaleMetricType codahaleMetricType,
                         String numerator,
                         String denominator,
                         String counterNumeratorOverride,
                         @Nullable String metricNameForCounterMetric,
                         String context,
                         String denominatorForRateMetrics,
                         boolean treatGaugeAsCounter,
                         boolean treatCounterAsAGauge) {
    super(name,
          label,
          description,
          numerator,
          denominator,
          context);
    Preconditions.checkNotNull(codahaleMetricType);
    Preconditions.checkArgument(
        CodahaleMetricType.TIMER != codahaleMetricType ||
        null != denominatorForRateMetrics);
    if (CodahaleMetricType.TIMER.equals(codahaleMetricType) ||
        CodahaleMetricType.HISTOGRAM.equals(codahaleMetricType)) {
      Preconditions.checkNotNull(counterNumeratorOverride);
    }
    if (CodahaleMetricType.GAUGE != codahaleMetricType) {
      // We don't allowing setting this to true for anything other than a
      // gauge.
      Preconditions.checkArgument(!treatGaugeAsCounter);
    }
    if (CodahaleMetricType.COUNTER != codahaleMetricType) {
      // We don't allowing setting this to true for anything other than a
      // counter.
      Preconditions.checkArgument(!treatCounterAsAGauge);
    }
    this.metricType = codahaleMetricType;
    this.numeratorForCounterMetric = counterNumeratorOverride;
    this.metricNameForCounterMetric = metricNameForCounterMetric;
    this.denominatorForRateMetrics = denominatorForRateMetrics;
    this.treatGaugeAsACounter = treatGaugeAsCounter;
    this.treatCounterAsAGauge = treatCounterAsAGauge;
  }

  @JsonProperty
  public String getMetricType() {
    return metricType.name();
  }

  @SuppressWarnings("UnusedDeclaration") // used by fasterxml
  @JsonProperty
  private void setMetricType(String metricType) {
    if (null == metricType) {
      return;
    }
    try {
      this.metricType = CodahaleMetricType.valueOf(metricType.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid metric type " + metricType);
    }
  }

  @JsonIgnore
  public CodahaleMetricType getType() {
    return metricType;
  }

  @JsonProperty
  public String getNumeratorForCounterMetric() {
    return numeratorForCounterMetric;
  }

  @JsonProperty
  private void setNumeratorForCounterMetric(String numeratorForCounterMetric) {
    this.numeratorForCounterMetric = numeratorForCounterMetric;
  }

  @JsonProperty
  public String getMetricNameForCounterMetric() {
    return metricNameForCounterMetric;
  }

  @JsonProperty
  private void setMetricNameForCounterMetric(
      @Nullable String metricNameForCounterMetric) {
    this.metricNameForCounterMetric = metricNameForCounterMetric;
  }

  @JsonProperty
  public String getDenominatorForRateMetrics() {
    return denominatorForRateMetrics;
  }

  @JsonProperty
  private void setDenominatorForRateMetrics(String denominatorForRateMetrics) {
    this.denominatorForRateMetrics = denominatorForRateMetrics;
  }

  /**
   * Returns the context for the codahale metric. If context is 'null' the
   * 'name' of the codahale metric is used. The full context for each Cloudera
   * Manager metric is constructed in the CodahaleMetricAdapter.
   * @return
   */
  @Override
  @JsonProperty
  public String getContext() {
    return null == context ? name : context;
  }

  @JsonProperty
  public boolean getTreatGaugeAsACounter() {
    return treatGaugeAsACounter;
  }

  @JsonProperty
  private void setTreatGaugeAsACounter(boolean treatGaugeAsACounter) {
    this.treatGaugeAsACounter = treatGaugeAsACounter;
  }

  @JsonProperty
  public boolean getTreatCounterAsAGauge() {
    return treatCounterAsAGauge;
  }

  @JsonProperty
  private void setTreatCounterAsAGauge(boolean treatCounterAsAGauge) {
    this.treatCounterAsAGauge = treatCounterAsAGauge;
  }
}