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
package com.cloudera.csd.descriptors;

import com.cloudera.csd.descriptors.InterfaceStability.Unstable;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesFormat;
import com.cloudera.csd.validation.references.annotations.Named;

import java.util.List;

import javax.validation.Valid;

/**
 * The root interface that describes monitoring definitions for a role. This
 * exists primarily to allow non-CSD-based services to define their monitoring
 * definitions in a CSD-like manner and for use in automated production of the
 * monitoring-related portions of a CSD.
 */
@Named
@Unstable
public interface RoleMonitoringDefinitionsDescriptor {

  /**
   * The name of the role. If this descriptor is part of a CSD bundle this
   * should match the role name in one of the {@RoleDescriptor}.
   * @return
   */
  @EntityTypeFormat
  String getName();

  /**
   * Optional. If set, specifies metrics that will be registered for this role.
   * These are in addition to default role metrics defined for all roles by
   * Cloudera Manager.
   * @return
   */
  @Valid
  @UniqueField("name")
  List<MetricDescriptor> getMetricDefinitions();

  /**
   * Returns the string to use to pluralize the name of the role for cross
   * entity aggregate metrics. For example, for a role named "ECHO_PUTTY"
   * this should be "echo_putties". Cross entity aggregate metric names will be
   * composed using this to generate metrics named like
   * 'fd_open_across_echo_putties'.
   *
   * The string must be prefixed with the service name and be unique within this
   * descriptor.
   *
   * If this is not specified the name will be constructed from the service
   * name, the role name, and an "s".
   * @return
   */
  @Valid
  @NameForCrossEntityAggregatesFormat
  String getNameForCrossEntityAggregateMetrics();
}
