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

import com.cloudera.csd.descriptors.generators.ConfigEntry;
import com.cloudera.csd.validation.constraints.DeprecationChecks;
import com.cloudera.csd.validation.constraints.RequiresSubdir;

import java.util.List;

import javax.validation.Valid;

/**
 * Describes the logging context.
 */
public interface GatewayLoggingDescriptor {

  /**
   * Optional. Filename of the logging configuration file. If not specified,
   * uses a default file name as specified in {@link CsdLoggingType}.
   *
   * @return the filename
   */
  @RequiresSubdir(groups = DeprecationChecks.class)
  String getConfigFilename();

  /**
   * Logging type used by the entity using this descriptor.
   */
  CsdLoggingType getLoggingType();

  /**
   * Emitted after parameter configs, before safety valves.
   */
  @Valid
  List<ConfigEntry> getAdditionalConfigs();
}
