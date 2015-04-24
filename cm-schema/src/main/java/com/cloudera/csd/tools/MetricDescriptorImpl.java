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

import com.google.common.base.Preconditions;

/**
 * A simple implementation of the MetricDescriptor interface that can be used by
 * the different adapters.
 */
public class MetricDescriptorImpl implements MetricDescriptor {
  private final String name;
  private final String label;
  private final String description;
  private final String numeratorUnit;
  private final String denominatorUnit;
  private final boolean isCounter;
  private final String weightingMetricName;
  private final String context;

  /**
   * A simpler builder class to build a MetricDescriptor. No validation is done
   * on the input.
   */
  public static class Builder {
    private String name;
    private String label;
    private String description;
    private String numeratorUnit;
    private String denominatorUnit;
    private boolean isCounter;
    private String weightingMetricName;
    private String context;

    public Builder setName(String serviceName, String name) {
      Preconditions.checkNotNull(serviceName);
      Preconditions.checkNotNull(name);
      this.name = String.format("%s_%s",
                                serviceName.toLowerCase(),
                                name.toLowerCase());
      return this;
    }

    public Builder setLabel(String label) {
      Preconditions.checkNotNull(label);
      Preconditions.checkArgument(!label.isEmpty());
      this.label = label;
      return this;
    }

    public Builder setDescription(String description) {
      Preconditions.checkNotNull(description);
      Preconditions.checkArgument(!description.isEmpty());
      this.description = description;
      return this;
    }

    public Builder setNumeratorUnit(String numeratorUnit) {
      Preconditions.checkNotNull(numeratorUnit);
      Preconditions.checkArgument(!numeratorUnit.isEmpty());
      this.numeratorUnit = numeratorUnit;
      return this;
    }

    public Builder setDenominatorUnit(String denominatorUnit) {
      this.denominatorUnit = denominatorUnit;
      return this;
    }

    public Builder setIsCounter(boolean isCounter) {
      this.isCounter = isCounter;
      return this;
    }

    public Builder setWeightingMetricName(String weightingMetricName) {
      this.weightingMetricName = weightingMetricName;
      return this;
    }

    public Builder setContext(String context) {
      this.context = context;
      return this;
    }

    public MetricDescriptorImpl build() {
      return new MetricDescriptorImpl(name,
                                      label,
                                      description,
                                      numeratorUnit,
                                      denominatorUnit,
                                      isCounter,
                                      weightingMetricName,
                                      context);
    }
  }

  public MetricDescriptorImpl(
      String name,
      String label,
      String description,
      String numeratorUnit,
      String denominatorUnit,
      boolean isCounter,
      String weightingMetricName,
      String context) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isEmpty());
    Preconditions.checkNotNull(label);
    Preconditions.checkArgument(!label.isEmpty());
    Preconditions.checkNotNull(description);
    Preconditions.checkArgument(!description.isEmpty());
    Preconditions.checkNotNull(numeratorUnit);
    Preconditions.checkArgument(!numeratorUnit.isEmpty());
    this.name = name;
    this.label = label;
    this.description = description;
    this.numeratorUnit = numeratorUnit;
    this.denominatorUnit = denominatorUnit;
    this.isCounter = isCounter;
    this.weightingMetricName = weightingMetricName;
    this.context = context;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getNumeratorUnit() {
    return numeratorUnit;
  }

  @Override
  public String getDenominatorUnit() {
    return denominatorUnit;
  }

  @Override
  public boolean isCounter() {
    return isCounter;
  }

  @Override
  public String getWeightingMetricName() {
    return weightingMetricName;
  }

  @Override
  public String getContext() {
    return context;
  }
}
