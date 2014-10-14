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
package com.cloudera.csd.descriptors.parameters;

import com.cloudera.validation.constraints.FilePermission;

import javax.validation.constraints.NotNull;

public interface PathParameter extends StringParameter {

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
  @FilePermission
  String getMode();
}
