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
package com.cloudera.csd.descriptors.parameters;

import org.hibernate.validator.constraints.Range;

/**
 * Parameter representing a role memory quantity.
 * <p>
 * MemoryParameters allow CM to gauge a role's memory consumption and to include
 * the role within any memory-related automatic configuration workflows.
 * <p>
 * A role's memory consumption is equal to the sum of all memory parameters,
 * multiplied by their scale factors.
 * <p>
 * In memory configuration workflows, each role and MemoryParameter pair is
 * assigned a "minimum" and an "ideal" value. On each host, all minimums and all
 * ideals are summed, then compared to the actual host RAM. The ratio between
 * what's available and what's requested is used to dictate where on the
 * minimum<-->ideal spectrum to place the actual value. For example, on a host
 * where sum(ideals) exceeds the host RAM, the configured values will simply be
 * the ideal values. Conversely, if sum(minimums) exceeds host RAM, all values
 * will be set to the minimums.
 * <p>
 * If a role is not directly involved in a workflow, its existing memory
 * consumption will still be accounted for as described earlier.
 * <p>
 * For all memory configuration workflows, a MemoryParameter's default field
 * must be set for the parameter to participate. For resource management (RM)
 * workflows, the auto config share field must also be set.
 * <p>
 * In non-RM workflows, the {role, parameter} pair's minimum is the
 * parameter's min field if set, or 0 otherwise. The ideal is the parameter's
 * default field.
 * <p>
 * In RM workflows, the minimum is the parameter's default field. The ideal is
 * computed using the following formula:
 *
 *   min(parameter.softMax (if exists) or parameter.max (if exists) or Long.MAX_VALUE,
 *       host_ram * 0.8 / parameter.scaleFactor * service_percentage * parameter.autoConfigShare)
 *
 * host_ram is equal to the host's RAM, while service_percentage is equal to
 * the percentage (up to 100) assigned to the overall service by the user during
 * the workflow.
 */
public interface MemoryParameter extends LongParameter {
  /**
   * Factor used in memory consumption calculation to account for any inherent
   * overhead in the memory quantity. The consumption is calculated by
   * multiplying the scale factor with the value of the parameter.
   * <p>
   * For JVMs, a suggested scale factor is 1.3.
   * <p>
   * Defaults to 1.0.
   */
  Double getScaleFactor();

  /**
   * During autoconfiguration for RM, the share dictates the percentage of the
   * role's overall memory allotment that should be set aside for this memory
   * quantity.
   * <p>
   * If null, parameter is not autoconfigured for RM.
   */
  @Range(min = 0, max = 100)
  Integer getAutoConfigShare();
}
