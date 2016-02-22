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
package com.cloudera.csd.validation.monitoring;

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * A class to hold state accessible to monitoring validators during descriptor
 * validation.
 */
public class MonitoringValidationContext {

  public final ServiceMonitoringDefinitionsDescriptor serviceDescriptor;
  public final ImmutableMap<String, MetricDescriptor> metricsDefined;
  public final ImmutableMap<String, MetricEntityTypeDescriptor> entitiesDefined;
  public final ImmutableMap<String, RoleMonitoringDefinitionsDescriptor> rolesDefined;
  public final ImmutableMap<String, MetricEntityAttributeDescriptor> attributesDefined;

  public MonitoringValidationContext(
      ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
    Preconditions.checkNotNull(serviceDescriptor);
    this.serviceDescriptor = serviceDescriptor;
    ImmutableMap.Builder<String, RoleMonitoringDefinitionsDescriptor>
      rolesDefinedBuilder = ImmutableMap.builder();
    // We can't use an ImmutableMap.Builder since it will not allow multiple
    // entries with the same key. Instead we build a local hash map and make
    // it immutable below.
    Map<String, MetricDescriptor> metricsDefinedBuilder = Maps.newHashMap();
    metricsDefinedBuilder.putAll(
        extractMetrics(serviceDescriptor.getMetricDefinitions()));
    if (null != serviceDescriptor.getRoles()) {
      for (RoleMonitoringDefinitionsDescriptor role :
           serviceDescriptor.getRoles()) {
        rolesDefinedBuilder.put(
            MonitoringConventions.getRoleMetricEntityTypeName(
                serviceDescriptor.getName(),
                role.getName()),
            role);
        metricsDefinedBuilder.putAll(
            extractMetrics(role.getMetricDefinitions()));
      }
    }
    if (null != serviceDescriptor.getMetricEntityTypeDefinitions()) {
      for (MetricEntityTypeDescriptor entity :
          serviceDescriptor.getMetricEntityTypeDefinitions()) {
        metricsDefinedBuilder.putAll(
            extractMetrics(entity.getMetricDefinitions()));
      }
    }
    ImmutableMap.Builder<String, MetricEntityTypeDescriptor>
      entitiesDefinedBuilder = ImmutableMap.builder();
    if (null != serviceDescriptor.getMetricEntityTypeDefinitions()) {
      for (MetricEntityTypeDescriptor entity :
           serviceDescriptor.getMetricEntityTypeDefinitions()) {
        entitiesDefinedBuilder.put(entity.getName(), entity);
      }
    }
    ImmutableMap.Builder<String, MetricEntityAttributeDescriptor>
      attributesDefinedBuilder = ImmutableMap.builder();
    if (null != serviceDescriptor.getMetricEntityAttributeDefinitions()) {
      for (MetricEntityAttributeDescriptor attribute :
           serviceDescriptor.getMetricEntityAttributeDefinitions()) {
        attributesDefinedBuilder.put(attribute.getName(), attribute);
      }
    }
    metricsDefined = ImmutableMap.copyOf(metricsDefinedBuilder);
    rolesDefined = rolesDefinedBuilder.build();
    entitiesDefined = entitiesDefinedBuilder.build();
    attributesDefined = attributesDefinedBuilder.build();
  }

  private Map<String, MetricDescriptor> extractMetrics(
      @Nullable List<MetricDescriptor> metrics) {
    if (null == metrics) {
      return ImmutableMap.of();
    }
    Map<String, MetricDescriptor> ret = Maps.newHashMap();
    for (MetricDescriptor metric : metrics) {
      ret.put(metric.getName(), metric);
    }
    return ret;
  }
}
