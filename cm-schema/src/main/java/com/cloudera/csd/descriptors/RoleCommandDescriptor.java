// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Describes a role command used by the CSD framework.
 */
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
