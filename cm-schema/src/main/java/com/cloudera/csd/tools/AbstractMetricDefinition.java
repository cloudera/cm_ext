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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * A helper class defining the metadata for a single metric definition from an
 * instrumentation framework.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractMetricDefinition {

  public static class Builder<S extends AbstractMetricDefinition.Builder<?>> {
    protected String name;
    protected String label;
    protected String description;
    protected String numerator;
    protected String denominator;
    protected String context;

    @SuppressWarnings("unchecked")
    public S setName(String name) {
      Preconditions.checkNotNull(name);
      this.name = name;
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setLabel(String label) {
      Preconditions.checkNotNull(label);
      this.label = label;
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setDescription(String description) {
      Preconditions.checkNotNull(description);
      this.description = description;
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setNumerator(String numerator) {
      Preconditions.checkNotNull(numerator);
      this.numerator = numerator;
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setDenominator(String denominator) {
      this.denominator = denominator;
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setContext(String context) {
      this.context = context;
      return (S) this;
    }
  }

  // The name of the metric.
  @JsonIgnore
  protected String name;

  // The  display name of the metric.
  @JsonIgnore
  protected String label;

  // The description for the metric.
  @JsonIgnore
  protected String description;

  // A string used for the numerator display name. For example, "bytes",
  // "calls", "partitions".
  @JsonIgnore
  protected String numeratorUnit;

  // The string used for the denominator display name. For example, "seconds",
  // "minutes", "yards".
  @JsonIgnore
  protected String denominatorUnit;

  // The context for the metric. The full context for each Cloudera Manager
  // metric is constructed in the CodahaleMetricAdapter.
  @JsonIgnore
  protected String context;

  @JsonCreator
  protected AbstractMetricDefinition() {
  }

  protected AbstractMetricDefinition(String name,
                                     String label,
                                     String description,
                                     String numerator,
                                     String denominator,
                                     String context) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(label);
    Preconditions.checkNotNull(description);
    Preconditions.checkNotNull(numerator);
    this.name = name;
    this.label = label;
    this.description = description;
    this.numeratorUnit = numerator;
    this.denominatorUnit = denominator;
    this.context = context;
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
  public String getDenominatorUnit() {
    return denominatorUnit;
  }

  @JsonProperty
  public void setDenominatorUnit(String denominatorUnit) {
    this.denominatorUnit = denominatorUnit;
  }

  @JsonProperty
  public String getContext() {
    return context;
  }

  @JsonProperty
  public void setContext(String context) {
    this.context = context;
  }
}