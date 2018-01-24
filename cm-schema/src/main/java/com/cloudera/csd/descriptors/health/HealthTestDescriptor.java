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

import com.cloudera.csd.validation.constraints.EntityTypeFormat;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Defines a health test that act on stored metrics and depending on its
 * criteria result in one of the following state:.
 * <ol>
 * <li>Green : meaning the test is healthy
 * <li>Yellow : meaning the test is concerning, requiring investigation at some point
 * <li>Red : meaning the test is critical, requiring immediate attention
 * <li>Unavailable : possibly due to lack of data points or invalid configurations
 * </ol>
 */
public interface HealthTestDescriptor {

  /**
   * Unique name for the health test within this CSD. Need not be human readable.
   *
   * @return unique name
   */
  @NotEmpty
  @EntityTypeFormat
  public String getName();

  /**
   * Human readable short label for the health test.
   *
   * @return name
   */
  @NotEmpty
  public String getLabel();

  /**
   * Human readable description for the health test.
   *
   * @return description
   */
  @NotEmpty
  public String getDescription();

  @Valid
  public HealthTestAdviceDescriptor getAdvice();
}
