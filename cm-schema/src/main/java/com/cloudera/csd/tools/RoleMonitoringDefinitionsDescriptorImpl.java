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

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A helper class to serialize RoleMonitoringDefinitionsDescriptor. Generates
 * a stable, sorted, role descriptor.
 */
public class RoleMonitoringDefinitionsDescriptorImpl
    implements RoleMonitoringDefinitionsDescriptor {

  public static class Builder {
    private String name;
    private List<MetricDescriptor> metricDefinitions;
    private String nameForCrossEntityAggregateMetrics;
    private List<String> additionalImmutableAttributeNames;
    private List<String> additionalMutableAttributeNames;

    public Builder() {
    }

    public Builder(RoleMonitoringDefinitionsDescriptor role) {
      Preconditions.checkNotNull(role);
      this.name = role.getName();
      if (null != role.getMetricDefinitions()) {
        this.metricDefinitions = Lists.newArrayList(role.getMetricDefinitions());
      }
      this.nameForCrossEntityAggregateMetrics =
          role.getNameForCrossEntityAggregateMetrics();
      this.additionalImmutableAttributeNames =
          role.getAdditionalImmutableAttributeNames();
      this.additionalMutableAttributeNames =
          role.getAdditionalMutableAttributeNames();
    }

    public Builder setName(String name) {
      Preconditions.checkNotNull(name);
      this.name = name;
      return this;
    }

    public Builder setMetricDefinitions(
        List<MetricDescriptor> metricDefinitions) {
      Preconditions.checkNotNull(metricDefinitions);
      this.metricDefinitions = Lists.newArrayList(metricDefinitions);
      return this;
    }

    public Builder addMetricDefinitions(
        @Nullable Collection<MetricDescriptor> metricDefinitions) {
      if (null == metricDefinitions) {
        return this;
      }
      if (null == this.metricDefinitions) {
        this.metricDefinitions = Lists.newArrayList();
      }
      this.metricDefinitions.addAll(metricDefinitions);
      return this;
    }

    public Builder setNameForCrossEntityAggregateMetrics(
        @Nullable String nameForCrossEntityAggregateMetrics) {
      this.nameForCrossEntityAggregateMetrics =
          nameForCrossEntityAggregateMetrics;
      return this;
    }

    public Builder setAdditionalImmutableAttributeNames(
        @Nullable List<String> additionalImmutableAttributeNames) {
      this.additionalImmutableAttributeNames = additionalImmutableAttributeNames;
      return this;
    }

    public Builder setAdditionalMutableAttributeNames(
        @Nullable List<String> additionalMutableAttributeNames) {
      this.additionalMutableAttributeNames = additionalMutableAttributeNames;
      return this;
    }

    public RoleMonitoringDefinitionsDescriptor build() {
      if (null != metricDefinitions) {
        Collections.sort(metricDefinitions,
                         Comparators.METRIC_DESCRIPTOR_COMPARATOR);
      }
      return new RoleMonitoringDefinitionsDescriptorImpl(
          name,
          metricDefinitions,
          nameForCrossEntityAggregateMetrics,
          additionalImmutableAttributeNames,
          additionalMutableAttributeNames);
    }
  }

  private final String name;
  private final List<MetricDescriptor> metricDefinitions;
  private String nameForCrossEntityAggregateMetrics;
  private List<String> additionalImmutableAttributeNames;
  private List<String> additionalMutableAttributeNames;

  private RoleMonitoringDefinitionsDescriptorImpl(
      String name,
      List<MetricDescriptor> metricDefinitions,
      @Nullable String nameForCrossEntityAggregateMetrics,
      @Nullable List<String> additionalImmutableAttributeNames,
      @Nullable List<String> additionalMutableAttributeNames) {
    Preconditions.checkNotNull(name);
    this.name = name;
    this.metricDefinitions = metricDefinitions;
    this.nameForCrossEntityAggregateMetrics = nameForCrossEntityAggregateMetrics;
    this.additionalImmutableAttributeNames = additionalImmutableAttributeNames;
    this.additionalMutableAttributeNames = additionalMutableAttributeNames;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<MetricDescriptor> getMetricDefinitions() {
    return metricDefinitions;
  }

  @Override
  public String getNameForCrossEntityAggregateMetrics() {
    return nameForCrossEntityAggregateMetrics;
  }

  @Override
  public List<String> getAdditionalImmutableAttributeNames() {
    return additionalImmutableAttributeNames;
  }

  @Override
  public List<String> getAdditionalMutableAttributeNames() {
    return additionalMutableAttributeNames;
  }
}
