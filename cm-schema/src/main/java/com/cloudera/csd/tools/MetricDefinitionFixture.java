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
package com.cloudera.csd.tools;

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

/**
 * A helper class for defining an instrumentation-specific metrics fixture to
 * be used in producing ServiceMonitoringDefinitions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricDefinitionFixture
  <T extends AbstractMetricDefinition> {

  @JsonIgnore
  private String serviceName;
  @JsonIgnore
  private List<T> serviceMetrics;
  @JsonIgnore
  private Map<String, List<T>> rolesMetrics;
  @JsonIgnore
  private Map<String, List<T>> additionalServiceEntityTypesMetrics;
  @JsonIgnore
  private final Set<String> serviceMetricNames;
  @JsonIgnore
  private final Map<String, Set<String>> rolesMetricsNames;
  @JsonIgnore
  private final Map<String, Set<String>> entitiesMetricsNames;

  public MetricDefinitionFixture() {
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
  public List<T> getServiceMetrics() {
    return serviceMetrics;
  }

  @JsonProperty
  public void setServiceMetrics(List<T> serviceMetrics) {
    this.serviceMetrics = serviceMetrics;
  }

  @JsonProperty
  public Map<String, List<T>> getRolesMetrics() {
    return rolesMetrics;
  }

  @JsonProperty
  public void setRolesMetrics(Map<String, List<T>> rolesMetrics) {
    this.rolesMetrics = rolesMetrics;
  }

  @JsonProperty
  public Map<String, List<T>> getAdditionalServiceEntityTypesMetrics() {
    return additionalServiceEntityTypesMetrics;
  }

  @JsonProperty
  public void setAdditionalServiceEntityTypesMetrics(
      Map<String, List<T>> additionalServiceEntityTypesMetrics) {
    this.additionalServiceEntityTypesMetrics =
        additionalServiceEntityTypesMetrics;
  }

  @JsonIgnore
  public void addServiceMetric(T metric) {
    if (serviceMetricNames.contains(metric.getName())) {
      throw new IllegalArgumentException("Metric " + metric.getName() +
                                         "already added");
    }
    serviceMetrics.add(metric);
    serviceMetricNames.add(metric.getName());
  }

  @JsonIgnore
  public void addRoleMetric(String roleName, T metric) {
    addIfNew(roleName,
             metric,
             rolesMetricsNames,
             rolesMetrics);
  }

  @JsonIgnore
  public void addEntityMetric(String entityName, T metric) {
    addIfNew(entityName,
             metric,
             entitiesMetricsNames,
             additionalServiceEntityTypesMetrics);
  }

  private void addIfNew(String entity,
                        T metric,
                        Map<String, Set<String>> existing,
                        Map<String, List<T>> metrics) {
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(existing);
    Preconditions.checkNotNull(metrics);

    if (existing.containsKey(entity) &&
        existing.get(entity).contains(metric.getName())) {
      return;
    }
    List<T> entityMetrics = metrics.get(entity);
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
