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

import javax.validation.constraints.Min;

/**
 * Describes where this role can be deployed on
 * the cluster. This includes the number of instances,
 * and restrictions on co-location. If the topology
 * descriptor is not specified, then min instances are
 * set 1 and max instances are Integer.MAX_VALUE.
 *
 * TODO: Add validation on descriptor to check ranges
 * Need to make sure min < max
 */
public interface TopologyDescriptor {

  /** Defaults to 1 */
  @Min(0)
  Integer getMinInstances();

  /** Defaults to Integer.MAX_VALUE */
  @Min(1)
  Integer getMaxInstances();
}
