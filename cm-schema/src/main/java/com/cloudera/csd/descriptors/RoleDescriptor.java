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

import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.constraints.UniqueRoleType;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a role type used by the CSD
 * framework.
 */
public interface RoleDescriptor {

  @EntityTypeFormat
  @UniqueRoleType
  String getName();

  @NotBlank
  String getLabel();

  /**
   * The plural form of {@link #getLabel()}
   */
  @NotBlank
  String getPluralLabel();

  /**
   * The primary external link for this role.
   */
  @Valid
  RoleExternalLink getExternalLink();

  /**
   * Any additional external links that are
   * relevant to the role.
   */
  @Valid
  @UniqueField("name")
  Set<RoleExternalLink> getAdditionalExternalLinks();

  @NotNull
  @Valid
  RunnerDescriptor getStartRunner();

  @Valid
  TopologyDescriptor getTopology();

  @UniqueField.List({
    @UniqueField("name"),
    @UniqueField("configName")
  })
  @Valid
  Set<Parameter<?>> getParameters();

  @Valid
  RunAs getRunAs();

  @Valid
  LoggingDescriptor getLogging();

  @UniqueField("name")
  @Valid
  Set<RoleCommandDescriptor> getCommands();
  
  @Valid
  ConfigWriter getConfigWriter();
}
