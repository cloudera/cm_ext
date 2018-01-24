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
package com.cloudera.csd.descriptors.health;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Contains all the information needed to aggregate role level
 * health to the service level.
 */
public interface HealthAggregationDescriptor {

  /**
   * A type of health aggregation where the service health is
   * reported based the health of the singleton role type.
   * <p>
   * This should be only used for 'singleton' role types.
   */
  public interface SingletonAggregationDescriptor
      extends HealthAggregationDescriptor {
  }

  /**
   * A type of health aggregation where the service health is
   * reported based on the health of all the roles of this type.
   * <p>
   * This should be only used for 'non-singleton' role types.
   */
  public interface NonSingletonAggregationDescriptor
      extends HealthAggregationDescriptor {

    /**
     * A double value.
     * <p>
     * The associated service health will report "Good" (i.e GREEN)
     * if the total percentage of "Good" roles is strictly greater than this
     * value. If this condition evaluates to true, then
     * {@link NonSingletonAggregationDescriptor#getPercentYellowGreenForYellow()}
     * is not evaluated.
     */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("99.99")
    double getPercentGreenForGreen();

    /**
     * A double value.
     * <p>
     * The associated service health will report "Concerning" (i.e YELLOW)
     * if the total percentage of "Good" and "Concerning" role is strictly greater
     * than this value. If this condition evaluates to false then service health
     * would report as "Bad" (i.e RED).
     * <p>
     * Note:
     * <li>
     * <ul>This has lower precedence than percentGreenForGreen.</ul>
     * <ul>These rules imply that the health check will never return “Bad”
     * (i.e RED) unless at least one role is “Bad”.</ul>
     * </li>
     */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("99.99")
    double getPercentYellowGreenForYellow();
  }
}
