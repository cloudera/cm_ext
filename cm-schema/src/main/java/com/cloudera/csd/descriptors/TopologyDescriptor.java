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

import com.cloudera.csd.validation.constraints.Expression;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Describes where this role can be deployed on
 * the cluster. This includes the number of instances,
 * and restrictions on co-location. If the topology
 * descriptor is not specified, then min instances are
 * set 1 and max instances are Integer.MAX_VALUE.
 */
@Expression.List({
    @Expression("minInstances == null or softMinInstances == null or minInstances < softMinInstances"),
    @Expression("minInstances == null or softMaxInstances == null or minInstances <= softMaxInstances"),
    @Expression("minInstances == null or maxInstances == null or minInstances <= maxInstances"),
    @Expression("softMinInstances == null or softMaxInstances == null or softMinInstances <= softMaxInstances"),
    @Expression("softMinInstances == null or maxInstances == null or softMinInstances <= maxInstances"),
    @Expression("softMaxInstances == null or maxInstances == null or softMaxInstances < maxInstances")
})
public interface TopologyDescriptor {

  /** Minimum number of roles of this type allowed. Defaults to 1. */
  @Min(0)
  Integer getMinInstances();

  /** Maximum number of roles of this type allowed. Defaults to Integer.MAX_VALUE. */
  @Min(1)
  Integer getMaxInstances();

  /** Recommended minimum number of roles of this type. By default there is no recommended minimum. */
  @Min(1)
  Integer getSoftMinInstances();

  /** Recommended maximum number of roles of this type. By default there is no recommended maximum. */
  @Min(1)
  Integer getSoftMaxInstances();

  /**
   * Optional. Specify rules about where this role can be placed relative to other roles.
   */
  @Valid
  List<PlacementRuleDescriptor> getPlacementRules();
}
