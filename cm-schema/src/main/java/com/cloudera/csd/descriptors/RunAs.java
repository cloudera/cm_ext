// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes the user and group to use when executing
 * scripts by the agent.
 */
public interface RunAs {

  @NotBlank
  String getUser();

  @NotBlank
  String getGroup();
}
