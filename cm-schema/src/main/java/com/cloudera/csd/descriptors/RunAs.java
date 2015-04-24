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

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes the user and group to use when executing
 * scripts by the agent.
 */
public interface RunAs {

  /**
   * The user as which daemon and all processes on it run.
   * This specifies the default value of a parameter that gets
   * added for configuring the process user.
   * The value of this parameter is used to replace ${user} while
   * generating configuration.
   */
  @NotBlank
  String getUser();

  /**
   * The group as which daemon and all processes on it run.
   * This specifies the default value of a parameter that gets
   * added for configuring the process group.
   * The value of this parameter is used to replace ${group} while
   * generating configuration.
   */
  @NotBlank
  String getGroup();

  /**
   * The kerberos user as which daemon and all processes on it run.
   * This specifies the default value of a parameter that gets
   * added for configuring the kerberos principal.
   * The value of this parameter is used to replace ${principal} while
   * generating configuration.
   *
   * Note that ${principal} will have the same value as ${user} on
   * non-secure clusters.
   */
  String getPrincipal();
}
