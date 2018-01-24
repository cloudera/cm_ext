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

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Contains all the information needed to execute a custom program for
 * gracefully shutting down a role.
 * The 'standard stop' has a hardcoded timeout (we send a group sigkill
 * if the process is still running after that timeout).
 * When that is not enough, you can use this descriptor to specify a
 * different timeout, and optionally provide a custom shutdown program.
 * This is mutually exclusive with using GracefulStopDescriptor.
 */
public interface GracefulStopRoleDescriptor {

  /**
   * The default duration to wait for the shutdown program to complete.
   * If the program does not complete by then, the program will be aborted
   * and the processes will be exited abruptly (group sigkill).
   * @return timeout in milliseconds, must be greater or equal to zero.
   * Value of 0 means no timeout.
   */
  @Min(0)
  long getTimeout();

  /**
   * The shutdown program. If not specified, a standard kill script
   * will be used which is similar to the 'standard stop', i.e.
   * the only difference is you have control on the timeout
   */
  @Valid
  RunnerDescriptor getRunner();
}
