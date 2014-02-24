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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Contains all the information needed to execute a custom program for shutting
 * down processes.
 */
public interface GracefulStopDescriptor {

  /**
   * The duration to wait for the shutdown program to complete. If the program
   * does not complete by then, the program will be aborted and the processes
   * will be exited abruptly.
   *
   * @return the timeout in milliseconds, must be greater or equal to zero.
   * Value of 0 means no timeout.
   */
  @Min(0)
  long getTimeout();

  /**
   * Types of the roles that would actually be stopped by the shutdown program.
   *
   * @return the role types. No role types means all role types.
   */
  List<String> getRelevantRoleTypes();

  /**
   * The shutdown program.
   *
   * @return the runner
   */
  @NotNull
  RunnerDescriptor getRunner();

  /**
   * The master role name. This is used to determine when the stop
   * command is completed. When the one of the master roles is stopped
   * then the command is considered done.
   *
   * TODO: Add a check to make sure role name exists and is in the relevant role types.
   * @return the role name.
   */
  @NotBlank
  String getMasterRole();
}
