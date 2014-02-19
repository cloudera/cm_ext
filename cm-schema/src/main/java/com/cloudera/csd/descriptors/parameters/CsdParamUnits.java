// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

/**
 * Units for {@link Parameter}.
 */
public enum CsdParamUnits {
  // time
  MILLISECONDS,
  SECONDS,
  MINUTES,
  HOURS,
  // bytes
  BYTES,
  KILOBYTES,
  MEGABYTES,
  GIGABYTES,
  // other
  PERCENT,
  PAGES,
  TIMES,
  LINES;

  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
