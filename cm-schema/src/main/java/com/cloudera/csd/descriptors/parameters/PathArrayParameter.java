// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import javax.validation.constraints.NotNull;

public interface PathArrayParameter extends StringArrayParameter {

  /**
   * Type of path specified by this parameter. (REQUIRED)
   * <p>
   * If type is LOG_DIR or LOCAL_DATA_DIR, then CM will
   * auto-create this directory while starting daemon process.
   */
  @NotNull
  CsdPathType getPathType();

  /**
   * Mode of the path specified by this parameter. (0755 if not specified).
   *
   * <p>
   * If type is LOG_DIR or LOCAL_DATA_DIR, then CM will
   * auto-create this directory with the specified mode
   * while starting daemon process.
   */
  Integer getMode();
}
