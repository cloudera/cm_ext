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

import com.cloudera.csd.descriptors.CompatibilityDescriptor;
import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple implementation of the interface to help serializing an MDL. The
 * serialized MDL is stable that is all maps and lists are sorted.
 */
public class ServiceMonitoringDefinitionsDescriptorImpl implements
    ServiceMonitoringDefinitionsDescriptor {

  public static class Builder {
    private String name;
    private String version;
    private CompatibilityDescriptor compatibilityDescriptor;
    private List<RoleMonitoringDefinitionsDescriptor> roles;
    private List<MetricDescriptor> metricDefinitions;
    private List<MetricEntityAttributeDescriptor>
        metricEntityAttributeDefinitions;
    private List<MetricEntityTypeDescriptor> metricEntityTypeDefinition;
    private String nameForCrossEntityAggregateMetrics;

    public Builder() {
    }

    public Builder(ServiceMonitoringDefinitionsDescriptor mdl) {
      Preconditions.checkNotNull(mdl);
      this.name = mdl.getName();
      this.version = mdl.getVersion();
      this.compatibilityDescriptor = mdl.getCompatibility();
      if (null != mdl.getRoles()) {
        this.roles = Lists.newArrayList(mdl.getRoles());
      }
      if (null != mdl.getMetricDefinitions()) {
        this.metricDefinitions = Lists.newArrayList(mdl.getMetricDefinitions());
      }
      if (null != mdl.getMetricEntityAttributeDefinitions()) {
        this.metricEntityAttributeDefinitions =
            Lists.newArrayList(mdl.getMetricEntityAttributeDefinitions());
      }
      if (null != mdl.getMetricEntityTypeDefinitions()) {
        this.metricEntityTypeDefinition =
            Lists.newArrayList(mdl.getMetricEntityTypeDefinitions());
      }
      this.nameForCrossEntityAggregateMetrics =
          mdl.getNameForCrossEntityAggregateMetrics();
    }

    public Builder setName(String name) {
      Preconditions.checkNotNull(name);
      this.name = name;
      return this;
    }

    public Builder setVersion(String version) {
      Preconditions.checkNotNull(version);
      this.version = version;
      return this;
    }

    public Builder setCompatibility(
        CompatibilityDescriptor compatibilityDescriptor) {
      this.compatibilityDescriptor = compatibilityDescriptor;
      return this;
    }

    public Builder setRoles(List<RoleMonitoringDefinitionsDescriptor> roles) {
      Preconditions.checkNotNull(roles);
      this.roles = Lists.newArrayList(roles);
      return this;
    }

    public Builder setMetricDefinitions(
        Collection<MetricDescriptor> metricDefinitions) {
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

    public Builder setMetricEntityAttributeDescriptor(
        List<MetricEntityAttributeDescriptor> metricEntityAttributeDefinitions) {
      Preconditions.checkNotNull(metricEntityAttributeDefinitions);
      this.metricEntityAttributeDefinitions =
          Lists.newArrayList(metricEntityAttributeDefinitions);
      return this;
    }

    public Builder setMetricEntityTypeDescriptor(
        List<MetricEntityTypeDescriptor> metricEntityTypeDefinition) {
      Preconditions.checkNotNull(metricEntityTypeDefinition);
      this.metricEntityTypeDefinition =
          Lists.newArrayList(metricEntityTypeDefinition);
      return this;
    }

    public Builder setNameForCrossEntityAggregateMetrics(
        @Nullable String nameForCrossEntityAggregateMetrics) {
      this.nameForCrossEntityAggregateMetrics =
          nameForCrossEntityAggregateMetrics;
      return this;
    }

    public ServiceMonitoringDefinitionsDescriptor build() {
      if (null != metricDefinitions) {
        Collections.sort(metricDefinitions,
                         Comparators.METRIC_DESCRIPTOR_COMPARATOR);
      }
      if (null != roles) {
        Collections.sort(roles,
                         Comparators.ROLE_DESCRIPTOR_COMPARATOR);
      }
      if (null != metricEntityAttributeDefinitions) {
        Collections.sort(metricEntityAttributeDefinitions,
                         Comparators.ATTRIBUTE_DESCRIPTOR_COMPARATOR);
      }
      // The entity types are intentionally not sorted. Their order comes from
      // the base MDL.
      return new ServiceMonitoringDefinitionsDescriptorImpl(
          name,
          version,
          compatibilityDescriptor,
          roles,
          metricDefinitions,
          metricEntityAttributeDefinitions,
          metricEntityTypeDefinition,
          nameForCrossEntityAggregateMetrics);
    }
  }

  private final String name;
  private final String version;
  private final CompatibilityDescriptor compatibilityDescriptor;
  private final List<RoleMonitoringDefinitionsDescriptor> roles;
  private final List<MetricDescriptor> metricDefinitions;
  private final List<MetricEntityAttributeDescriptor>
      metricEntityAttributeDefinitions;
  private final List<MetricEntityTypeDescriptor> metricEntityTypeDefinition;
  private String nameForCrossEntityAggregateMetrics;

  private ServiceMonitoringDefinitionsDescriptorImpl(
      String name,
      String version,
      @Nullable CompatibilityDescriptor compatibilityDescriptor,
      @Nullable List<RoleMonitoringDefinitionsDescriptor> roles,
      @Nullable List<MetricDescriptor> metricDefinitions,
      @Nullable List<MetricEntityAttributeDescriptor>
          metricEntityAttributeDefinitions,
      @Nullable List<MetricEntityTypeDescriptor> metricEntityTypeDefinitions,
      @Nullable String nameForCrossEntityAggregateMetrics) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(version);
    Preconditions.checkArgument(!version.isEmpty());
    this.name = name;
    this.version = version;
    this.compatibilityDescriptor = compatibilityDescriptor;
    this.roles = roles;
    this.metricDefinitions = metricDefinitions;
    this.metricEntityAttributeDefinitions = metricEntityAttributeDefinitions;
    this.metricEntityTypeDefinition = metricEntityTypeDefinitions;
    this.nameForCrossEntityAggregateMetrics = nameForCrossEntityAggregateMetrics;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public CompatibilityDescriptor getCompatibility() {
    return compatibilityDescriptor;
  }

  @Override
  public List<RoleMonitoringDefinitionsDescriptor> getRoles() {
    return roles;
  }

  @Override
  public List<MetricDescriptor> getMetricDefinitions() {
    return metricDefinitions;
  }

  @Override
  public List<MetricEntityAttributeDescriptor>
  getMetricEntityAttributeDefinitions() {
    return metricEntityAttributeDefinitions;
  }

  @Override
  public List<MetricEntityTypeDescriptor> getMetricEntityTypeDefinitions() {
    return metricEntityTypeDefinition;
  }

  @Override
  public String getNameForCrossEntityAggregateMetrics() {
    return nameForCrossEntityAggregateMetrics;
  }
}
