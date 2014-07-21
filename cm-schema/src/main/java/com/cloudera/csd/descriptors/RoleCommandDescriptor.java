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

import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Describes a role command used by the CSD framework.
 */
@Named
@Referenced(type=ReferenceType.ROLE_COMMAND)
public interface RoleCommandDescriptor {

  @NotBlank
  String getName();

  @NotBlank
  String getLabel();

  @NotBlank
  String getDescription();

  @NotEmpty
  Set<Integer> getExpectedExitCodes();

  @NotNull
  RunnerDescriptor getCommandRunner();

  /**
   * Get the role state required in order for the command to be considered
   * available for execution.
   *
   * @return the required role state
   */
  @NotNull
  CsdRoleState getRequiredRoleState();
}
