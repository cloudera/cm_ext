// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Contains all the information needed to execute
 * a program on the agent.
 */
public interface RunnerDescriptor {

  @NotBlank
  String getProgram();

  List<String> getArgs();

  Map<String, String> getEnvironmentVariables();
}
