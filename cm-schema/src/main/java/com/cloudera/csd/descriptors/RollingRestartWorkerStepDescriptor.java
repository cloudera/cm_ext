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

import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referencing;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Specifies the steps involved in doing rolling restart of the given
 * worker role type.
 */
public interface RollingRestartWorkerStepDescriptor {

  /** Role for which the steps are applied during rolling restart. */
  @NotBlank
  @Referencing(type=ReferenceType.ROLE)
  String getRoleName();

  /**
   * List of service-level commands to run while bringing the role down during rolling
   * restart. If "Stop" is specified as one of the commands, the regular role
   * stop command is called.
   *
   * If this is not provided, role is simply stopped to bring it down.
   */
  @Referencing(type=ReferenceType.SERVICE_COMMAND)
  List<String> getBringDownCommands();

  /**
   * List of service-level commands to run while bringing the role up during rolling restart.
   * If "Start" is specified as one of the commands, the regular role start
   * command is called.
   *
   * If this is not provided, role is simply started to bring it up.
   */
  @Referencing(type=ReferenceType.SERVICE_COMMAND)
  List<String> getBringUpCommands();
}
