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
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A helper class to serialize MetricEntityTypeDescriptor.
 */
public class MetricEntityTypeDescriptorImpl
    implements MetricEntityTypeDescriptor {

  public static class Builder {
    private String name;
    private String nameForCrossEntityAggregateMetrics;
    private String label;
    private String plural;
    private String description;
    private List<String> immutableAttributeNames;
    private List<String> mutableAttributeNames;
    private List<String> entityNameFormat;
    private String entityLabelFormat;
    private List<String> parentMetricEntityTypeNames;
    private List<MetricDescriptor> metricDefinitions;

    public Builder() {
    }

    public Builder(MetricEntityTypeDescriptor source) {
      this.name = source.getName();
      this.nameForCrossEntityAggregateMetrics =
          source.getNameForCrossEntityAggregateMetrics();
      this.label = source.getLabel();
      this.plural = source.getLabelPlural();
      this.description = source.getDescription();
      if (null != source.getImmutableAttributeNames()) {
        this.immutableAttributeNames =
            Lists.newArrayList(source.getImmutableAttributeNames());
      }
      if (null != source.getMutableAttributeNames()) {
        this.mutableAttributeNames =
            Lists.newArrayList(source.getMutableAttributeNames());
      }
      if (null != source.getEntityNameFormat()) {
        this.entityNameFormat = Lists.newArrayList(source.getEntityNameFormat());
      }
      this.entityLabelFormat = source.getEntityLabelFormat();
      if (null != source.getParentMetricEntityTypeNames()) {
        this.parentMetricEntityTypeNames =
            Lists.newArrayList(source.getParentMetricEntityTypeNames());
      }
      if (null != source.getMetricDefinitions()) {
        this.metricDefinitions =
            Lists.newArrayList(source.getMetricDefinitions());
      }
    }

    public Builder setName(String name) {
      Preconditions.checkNotNull(name);
      this.name = name;
      return this;
    }

    public Builder setNameForCrossEntityAggregateMetrics(
        String nameForCrossEntityAggregateMetrics) {
      Preconditions.checkNotNull(nameForCrossEntityAggregateMetrics);
      this.nameForCrossEntityAggregateMetrics =
          nameForCrossEntityAggregateMetrics;
      return this;
    }

    public Builder setLabel(String label) {
      Preconditions.checkNotNull(label);
      this.label = label;
      return this;
    }

    public Builder setLabelPlural(String plural) {
      Preconditions.checkNotNull(plural);
      this.plural = plural;
      return this;
    }

    public Builder setDescription(String description) {
      Preconditions.checkNotNull(description);
      this.description = description;
      return this;
    }

    public Builder setImmutableAttributeNames(
        Collection<String> immutableAttributeNames) {
      Preconditions.checkNotNull(immutableAttributeNames);
      this.immutableAttributeNames = Lists.newArrayList(immutableAttributeNames);
      return this;
    }

    public Builder setMutableAttributeNames(
        Collection<String> mutableAttributeNames) {
      Preconditions.checkNotNull(mutableAttributeNames);
      this.mutableAttributeNames = Lists.newArrayList(mutableAttributeNames);
      return this;
    }

    public Builder setEntityNameFormat(List<String> entityNameFormat) {
      Preconditions.checkNotNull(entityNameFormat);
      this.entityNameFormat = Lists.newArrayList(entityNameFormat);
      return this;
    }

    public Builder setEntityLabelFormat(String entityLabelFormat) {
      Preconditions.checkNotNull(entityLabelFormat);
      this.entityLabelFormat = entityLabelFormat;
      return this;
    }

    public Builder setParentMetricEntityTypeNames(
        Collection<String> parentMetricEntityTypeNames) {
      Preconditions.checkNotNull(parentMetricEntityTypeNames);
      this.parentMetricEntityTypeNames =
          Lists.newArrayList(parentMetricEntityTypeNames);
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

    public MetricEntityTypeDescriptor build() {
      if (null != metricDefinitions) {
        Collections.sort(metricDefinitions,
                         Comparators.METRIC_DESCRIPTOR_COMPARATOR);
      }
      return new MetricEntityTypeDescriptorImpl(
          name,
          nameForCrossEntityAggregateMetrics,
          label,
          plural,
          description,
          immutableAttributeNames,
          mutableAttributeNames,
          entityNameFormat,
          entityLabelFormat,
          parentMetricEntityTypeNames,
          metricDefinitions);
    }
  }

  private final String name;
  private final String nameForCrossEntityAggregateMetrics;
  private final String label;
  private final String plural;
  private final String description;
  private final List<String> immutableAttributeNames;
  private final List<String> mutableAttributeNames;
  private final List<String> entityNameFormat;
  private final String entityLabelFormat;
  private final List<String> parentMetricEntityTypeNames;
  private final List<MetricDescriptor> metricDefinitions;

  private MetricEntityTypeDescriptorImpl(
      String name,
      String nameForCrossEntityAggregateMetrics,
      String label,
      String plural,
      String description,
      List<String> immutableAttributeNames,
      @Nullable List<String> mutableAttributeNames,
      List<String> entityNameFormat,
      @Nullable String entityLabelFormat,
      @Nullable List<String> parentMetricEntityTypeNames,
      @Nullable List<MetricDescriptor> metricDefinitions) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(nameForCrossEntityAggregateMetrics);
    Preconditions.checkNotNull(label);
    Preconditions.checkNotNull(plural);
    Preconditions.checkNotNull(description);
    Preconditions.checkNotNull(immutableAttributeNames);
    Preconditions.checkNotNull(entityNameFormat);
    this.name = name;
    this.nameForCrossEntityAggregateMetrics = nameForCrossEntityAggregateMetrics;
    this.label = label;
    this.plural = plural;
    this.description = description;
    this.immutableAttributeNames = immutableAttributeNames;
    this.mutableAttributeNames = mutableAttributeNames;
    this.entityNameFormat = entityNameFormat;
    this.entityLabelFormat = entityLabelFormat;
    this.parentMetricEntityTypeNames = parentMetricEntityTypeNames;
    this.metricDefinitions = metricDefinitions;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getNameForCrossEntityAggregateMetrics() {
    return nameForCrossEntityAggregateMetrics;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getLabelPlural() {
    return plural;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public List<String> getImmutableAttributeNames() {
    return immutableAttributeNames;
  }

  @Override
  public List<String> getMutableAttributeNames() {
    return mutableAttributeNames;
  }

  @Override
  public List<String> getEntityNameFormat() {
    return entityNameFormat;
  }

  @Override
  public String getEntityLabelFormat() {
    return entityLabelFormat;
  }

  @Override
  public List<String> getParentMetricEntityTypeNames() {
    return parentMetricEntityTypeNames;
  }

  @Override
  public List<MetricDescriptor> getMetricDefinitions() {
    return metricDefinitions;
  }
}
