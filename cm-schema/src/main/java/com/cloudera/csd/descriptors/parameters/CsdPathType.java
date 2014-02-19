// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

/**
 * The type of path to manage.
 */
public enum CsdPathType {
  /** Local data directory for daemon roles, auto-created while starting daemon process */
  LOCAL_DATA_DIR,
  /** Local data file for daemon roles. */
  LOCAL_DATA_FILE,
  /** Paths that are specific to the service. Usually not managed by CM. */
  SERVICE_SPECIFIC;

  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
