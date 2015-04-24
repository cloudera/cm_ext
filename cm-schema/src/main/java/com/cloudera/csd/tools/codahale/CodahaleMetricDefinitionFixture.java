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

import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.CodahaleMetricType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class that defines the metadata required for codahale metrics in order to
 * transform them to ServiceMonitoringDefinitions. Each codahale metric will be
 * transformed to one or more Cloudera Manager MetricDescriptor. For example,
 * a codahale histogram will be transformed to several MetricDescriptors for the
 * min, max, 99 percentile etc.
 */
@JsonIgnoreProperties
public class CodahaleMetricDefinitionFixture {

  private static final Logger LOG =
      LoggerFactory.getLogger(CodahaleMetricDefinitionFixture.class);

  /**
   * A helper class defining the metadata for a single codahale metric. Codahale
   * metrics are different than CSD metric descriptors as one codahale metric
   * is usually exposed as more than one CSD metric.
   */
  @JsonIgnoreProperties
  public static class CodahaleMetric {

    public static class Builder {
      private String name;
      private String label;
      private String description;
      private CodahaleMetricType codahaleMetricType;
      private String numerator;
      private String denominator;
      private String numeratorForCounterMetric;
      private String context;
      private String denominatorForRateMetrics;

      public Builder setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
        return this;
      }

      public Builder setLabel(String label) {
        Preconditions.checkNotNull(label);
        this.label = label;
        return this;
      }

      public Builder setDescription(String description) {
        Preconditions.checkNotNull(description);
        this.description = description;
        return this;
      }

      public Builder setCodahaleMetricType(
          CodahaleMetricType codahaleMetricType) {
        Preconditions.checkNotNull(codahaleMetricType);
        this.codahaleMetricType = codahaleMetricType;
        return this;
      }

      public Builder setNumerator(String numerator) {
        Preconditions.checkNotNull(numerator);
        this.numerator = numerator;
        return this;
      }

      public Builder setDenominator(String denominator) {
        this.denominator = denominator;
        return this;
      }

      public Builder setNumeratorForCounterMetric(
          String numeratorForCounterMetric) {
        this.numeratorForCounterMetric = numeratorForCounterMetric;
        return this;
      }

      public Builder setContext(String context) {
        this.context = context;
        return this;
      }

      public Builder setDenominatorForRateMetrics(
          String denominatorForRateMetrics) {
        this.denominatorForRateMetrics = denominatorForRateMetrics;
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
                                  context,
                                  denominatorForRateMetrics);
      }
    }


    // The name of the codahale metric. The name is used to construct the cm
    // metric name and for the context.
    @JsonIgnore
    private String name;

    // The  display name of the metric.
    @JsonIgnore
    private String label;

    // The description for the metric.
    @JsonIgnore
    private String description;

    // The type of the metric in the codahale metric system. Certain types are
    // transformed to multiple cm metrics.
    @JsonIgnore
    private CodahaleMetricType metricType;

    // A string used for the numerator display name. For example, "bytes",
    // "calls", "partitions".
    @JsonIgnore
    private String numeratorUnit;

    // A string to be used for the numerator display name for the counter metric
    // that is part of a codahale histogram or timer. For example for a
    // histogram that tracks the time it takes for processing an rpc call the
    // numerator can be "milliseconds". The counter unit (i.e, the number of
    // time the histogram update method was called) in this case can be "calls".
    // For a histogram or timer a value must be provided. This is ignored for
    // any other codahale metric types.
    @JsonIgnore
    private String numeratorForCounterMetric;

    // The string used for the denominator display name. For example, "seconds",
    // "minutes", "yards". Must be non-null for meter metrics.
    @JsonIgnore
    private String denominatorUnit;

    // the context for the codahale metric. If context is 'null' the 'name' of
    // the codahale metric is used. The full context for each Cloudera Manager
    // metric is constructed in the CodahaleMetricAdapter.
    @JsonIgnore
    private String context;

    // The string used for the denominator display name for rate metrics that
    // are part of a timer metrics. Timer metrics must include a non-null
    // value. This value is ignored for all other codahale metric types.
    @JsonIgnore
    private String denominatorForRateMetrics;

    @SuppressWarnings("UnusedDeclaration") // used by fasterxml
    private CodahaleMetric() {
    }

    private CodahaleMetric(String name,
                           String label,
                           String description,
                           CodahaleMetricType codahaleMetricType,
                           String numerator,
                           String denominator,
                           String counterNumeratorOverride,
                           String context,
                           String denominatorForRateMetrics) {
      Preconditions.checkNotNull(name);
      Preconditions.checkNotNull(label);
      Preconditions.checkNotNull(description);
      Preconditions.checkNotNull(codahaleMetricType);
      Preconditions.checkNotNull(numerator);
      Preconditions.checkArgument(
          CodahaleMetricType.TIMER != codahaleMetricType ||
          null != denominatorForRateMetrics);
      if (CodahaleMetricType.TIMER.equals(codahaleMetricType) ||
          CodahaleMetricType.HISTOGRAM.equals(codahaleMetricType)) {
        Preconditions.checkNotNull(counterNumeratorOverride);
      }
      this.name = name;
      this.label = label;
      this.description = description;
      this.metricType = codahaleMetricType;
      this.numeratorUnit = numerator;
      this.denominatorUnit = denominator;
      this.numeratorForCounterMetric = counterNumeratorOverride;
      this.context = context;
      this.denominatorForRateMetrics = denominatorForRateMetrics;
    }

    @JsonProperty
    public String getMetricType() {
      return metricType.name();
    }

    @SuppressWarnings("UnusedDeclaration") // used by fasterxml
    @JsonProperty
    public void setMetricType(String metricType) {
      if (null == metricType) {
        return;
      }
      try {
        this.metricType = CodahaleMetricType.valueOf(metricType.toUpperCase());
      } catch (IllegalArgumentException ex) {
        LOG.error("Invalid metric type " + metricType);
        throw ex;
      }
    }

    @JsonIgnore
    public CodahaleMetricType getType() {
      return metricType;
    }

    @JsonProperty
    public String getName() {
      return name;
    }

    @JsonProperty
    public void setName(String name) {
      this.name = name;
    }

    @JsonProperty
    public String getLabel() {
      return label;
    }

    @JsonProperty
    public void setLabel(String label) {
      this.label = label;
    }

    @JsonProperty
    public String getDescription() {
      return description;
    }

    @JsonProperty
    public void setDescription(String description) {
      this.description = description;
    }

    @JsonProperty
    public String getNumeratorUnit() {
      return numeratorUnit;
    }

    @JsonProperty
    public void setNumeratorUnit(String numeratorUnit) {
      this.numeratorUnit = numeratorUnit;
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
    public String getDenominatorUnit() {
      return denominatorUnit;
    }

    @JsonProperty
    public void setDenominatorUnit(String denominatorUnit) {
      this.denominatorUnit = denominatorUnit;
    }

    /**
     * Returns the context for the codahale metric. If context is 'null' the
     * 'name' of the codahale metric is used. The full context for each Cloudera
     * Manager metric is constructed in the CodahaleMetricAdapter.
     * @return
     */
    @JsonProperty
    public String getContext() {
      return null == context ? name : context;
    }

    @JsonProperty
    public void setContext(String context) {
      this.context = context;
    }

    @JsonProperty
    public String getDenominatorForRateMetrics() {
      return denominatorForRateMetrics;
    }

    @JsonProperty
    public void setDenominatorForRateMetrics(String denominatorForRateMetrics) {
      this.denominatorForRateMetrics = denominatorForRateMetrics;
    }
  }

  @JsonIgnore
  private String serviceName;
  @JsonIgnore
  private List<CodahaleMetric> serviceMetrics;
  @JsonIgnore
  private Map<String, List<CodahaleMetric>> rolesMetrics;
  @JsonIgnore
  private Map<String, List<CodahaleMetric>> additionalServiceEntityTypesMetrics;
  @JsonIgnore
  private final Set<String> serviceMetricNames;
  @JsonIgnore
  private final Map<String, Set<String>> rolesMetricsNames;
  @JsonIgnore
  private final Map<String, Set<String>> entitiesMetricsNames;

  public CodahaleMetricDefinitionFixture() {
    serviceMetricNames = Sets.newHashSet();
    serviceMetrics = Lists.newArrayList();
    rolesMetricsNames = Maps.newHashMap();
    rolesMetrics = Maps.newHashMap();
    entitiesMetricsNames = Maps.newHashMap();
    additionalServiceEntityTypesMetrics = Maps.newHashMap();
  }

  @JsonProperty
  public String getServiceName() {
    return serviceName;
  }

  @JsonProperty
  public void setServiceName(String serviceName) {
    if (null == serviceName || serviceName.isEmpty()) {
      throw new IllegalArgumentException("Invalid empty or null service name");
    }
    this.serviceName = serviceName;
  }

  @JsonProperty
  public List<CodahaleMetric> getServiceMetrics() {
    return serviceMetrics;
  }

  @JsonProperty
  public void setServiceMetrics(List<CodahaleMetric> serviceMetrics) {
    this.serviceMetrics = serviceMetrics;
  }

  @JsonProperty
  public Map<String, List<CodahaleMetric>> getRolesMetrics() {
    return rolesMetrics;
  }

  @JsonProperty
  public void setRolesMetrics(Map<String, List<CodahaleMetric>> rolesMetrics) {
    this.rolesMetrics = rolesMetrics;
  }

  @JsonProperty
  public Map<String, List<CodahaleMetric>> getAdditionalServiceEntityTypesMetrics() {
    return additionalServiceEntityTypesMetrics;
  }

  @JsonProperty
  public void setAdditionalServiceEntityTypesMetrics(
      Map<String, List<CodahaleMetric>> additionalServiceEntityTypesMetrics) {
    this.additionalServiceEntityTypesMetrics =
        additionalServiceEntityTypesMetrics;
  }

  @JsonIgnore
  public void addServiceMetric(CodahaleMetric metric) {
    if (serviceMetricNames.contains(metric.getName())) {
      throw new IllegalArgumentException("Metric " + metric.getName() +
                                         "already added");
    }
    serviceMetrics.add(metric);
    serviceMetricNames.add(metric.getName());
  }

  @JsonIgnore
  public void addRoleMetric(String roleName, CodahaleMetric metric) {
    addIfNew(roleName,
             metric,
             rolesMetricsNames,
             rolesMetrics);
  }

  @JsonIgnore
  public void addEntityMetric(String entityName, CodahaleMetric metric) {
    addIfNew(entityName,
             metric,
             entitiesMetricsNames,
             additionalServiceEntityTypesMetrics);
  }

  private void addIfNew(String entity,
                        CodahaleMetric metric,
                        Map<String, Set<String>> existing,
                        Map<String, List<CodahaleMetric>> metrics) {
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(existing);
    Preconditions.checkNotNull(metrics);

    if (existing.containsKey(entity) &&
        existing.get(entity).contains(metric.getName())) {
      return;
    }
    List<CodahaleMetric> entityMetrics = metrics.get(entity);
    Set<String> entityMetricsNames = existing.get(entity);
    if (null == entityMetrics) {
      entityMetrics = Lists.newArrayList();
      entityMetricsNames = Sets.newHashSet();
      metrics.put(entity, entityMetrics);
      existing.put(entity, entityMetricsNames);
    }
    entityMetrics.add(metric);
    entityMetricsNames.add(metric.getName());
  }
}
