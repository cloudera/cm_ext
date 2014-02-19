// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

/** Possible states of a role. */
public enum CsdRoleState {
  RUNNING,
  STOPPED;

  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
