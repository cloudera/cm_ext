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
import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.google.common.base.Preconditions;

import java.util.Comparator;

/**
 * This class provides a collection of comparators useful for sorting various
 * parts of our service monitoring definitions.
 */
public class Comparators {

  /**
   * A comparator for metric descriptors. This sorts on the metric name.
   */
  static final Comparator<MetricDescriptor>
    METRIC_DESCRIPTOR_COMPARATOR =
      new Comparator<MetricDescriptor>() {
    @Override
    public int compare(MetricDescriptor metricDescriptor1,
                       MetricDescriptor metricDescriptor2) {
      Preconditions.checkNotNull(metricDescriptor1);
      Preconditions.checkNotNull(metricDescriptor2);
      return metricDescriptor1.getName().compareTo(metricDescriptor2.getName());
    }
  };

  /**
   * A comparator for role monitoring definitions descriptors. This sorts on
   * the role name.
   */
  static final Comparator<RoleMonitoringDefinitionsDescriptor>
    ROLE_DESCRIPTOR_COMPARATOR =
      new Comparator<RoleMonitoringDefinitionsDescriptor>() {
    @Override
    public int compare(RoleMonitoringDefinitionsDescriptor role1,
                       RoleMonitoringDefinitionsDescriptor role2) {
      Preconditions.checkNotNull(role1);
      Preconditions.checkNotNull(role2);
      return role1.getName().compareTo(role2.getName());
    }
  };

  /**
   * A comparator for attribute descriptors. This sorts on the name.
   */
  static final Comparator<MetricEntityAttributeDescriptor>
    ATTRIBUTE_DESCRIPTOR_COMPARATOR =
      new Comparator<MetricEntityAttributeDescriptor>() {
    @Override
    public int compare(MetricEntityAttributeDescriptor attributeDescriptor1,
                       MetricEntityAttributeDescriptor attributeDescriptor2) {
      Preconditions.checkNotNull(attributeDescriptor1);
      Preconditions.checkNotNull(attributeDescriptor2);
      return attributeDescriptor1.getName().compareTo(
          attributeDescriptor2.getName());
    }
  };
}
