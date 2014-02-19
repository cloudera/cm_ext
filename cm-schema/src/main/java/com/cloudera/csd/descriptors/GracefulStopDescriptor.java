// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
