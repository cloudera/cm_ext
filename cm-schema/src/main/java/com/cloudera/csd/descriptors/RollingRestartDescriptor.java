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

import java.util.List;

/**
 * Specifies the workflow to rolling restart a service. If a service supports
 * rolling restart, this workflow must restart every daemon role within the service.
 * So all roles of the service must be specified in either worker or non-worker steps.
 * Roles can be restarted either one-by-one if they are non-worker roles,
 * or in batches if they are worker roles.
 */
public interface RollingRestartDescriptor {

  /**
   * Specifies the steps performed to rolling restart non-worker roles. Roles of
   * different types are restarted in the order they are specified in here, and
   * within each role type, roles are ordered by their hostnames.
   */
  List<RollingRestartNonWorkerStepDescriptor> getNonWorkerSteps();

  /**
   * Specifies the steps performed to rolling restart worker roles. Worker roles
   * are restarted in batches and batch size is specified by the user while
   * triggering rolling restart.
   *
   * Since the commands are applied to multiple worker roles at the same time, they
   * should all be service level commands.
   */
  RollingRestartWorkerStepDescriptor getWorkerSteps();
}
