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
import com.cloudera.csd.validation.constraints.MetricEntityTypeFormat;
import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesFormat;
import com.cloudera.csd.validation.references.annotations.Named;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Describes an entity type and a set of metrics associated with that type.
 */
@Named
@Unstable
public interface MetricEntityTypeDescriptor {

  /**
   * Returns the name of the entity type. This name uniquely identifies this
   * entity type and is used to reference the metric in the Cloudera Manager
   * API and charting features.
   * @return
   */
  @MetricEntityTypeFormat
  String getName();

  /**
   * Returns the string to use to pluralize the name of the entity for cross
   * entity aggregate metrics. For example, for an entity named "ECHO_ENTITY"
   * this should be "echo_entities". Cross entity aggregate metric names will be
   * composed using this to generate metrics named like
   * 'fd_open_across_echo_entities'.
   *
   * The string must be prefixed with the service name and be unique within this
   * descriptor.
   *
   * If this is not specified the name will be constructed from the service
   * name, the entity name, and an "s".
   * @return
   */
  @NameForCrossEntityAggregatesFormat
  String getNameForCrossEntityAggregateMetrics();

  /**
   * Returns the display name of the entity type.
   * @return
   */
  @NotEmpty
  String getLabel();

  /**
   * Returns the display name of the entity type in plural form.
   * @return
   */
  @NotEmpty
  String getLabelPlural();

  /**
   * Returns the description of the entity type.
   * @return
   */
  @NotEmpty
  String getDescription();

  /**
   * Returns the list immutable attributes for this entity type. Immutable
   * attributes values for an entity may not change over its lifetime.
   * @return
   */
  @NotEmpty
  List<String> getImmutableAttributeNames();

  /**
   * Returns the list mutable attributes for this entity type. Mutable
   * attributes for an entity may change over its lifetime.
   * @return
   */
  List<String> getMutableAttributeNames();

  /**
   * Returns a list of attribute names that will be used to construct entity
   * names for entities of this type. The attributes named here must be immutable
   * attributes of this type or a parent type.
   * @return
   */
  @NotEmpty
  List<String> getEntityNameFormat();

  /**
   * Returns a format string that will be used to construct the display name of
   * entities of this type. If this returns null the entity name would be used
   * as the display name.
   *
   * The entity attribute values are used to replace $attribute name portions of
   * this format string. For example, an entity with roleType "DATANODE" and
   * hostname "foo.com" will have a display name "DATANODE (foo.com)" if the
   * format is "$roleType ($hostname)".
   * @return
   */
  String getEntityLabelForamt();

  /**
   * Returns a list of metric entity type names which are parents of this
   * metric entity type. A metric entity type inherits the attributes of
   * its ancestors. For example a role metric entity type has its service as a
   * parent. A service metric entity type has a cluster as a parent. The role
   * type inherits its cluster name attribute through its service parent. Only
   * parent ancestors should be returned here. In the example given, only the
   * service metric entity type should be specified in the parent list.
   */
  List<String> getParentMetricEntityTypeNames();

  /**
   * Specifies metrics that will be registered for this metric entity type.
   * @return
   */
  @UniqueField("name")
  List<MetricDescriptor> getMetricDefinitions();
}
