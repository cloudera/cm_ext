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

import com.cloudera.csd.descriptors.cgroups.CgroupDescriptor;
import com.cloudera.csd.descriptors.parameters.BasicParameter;
import com.cloudera.csd.validation.constraints.AutoConfigSharesValid;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.constraints.UniqueRoleType;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a role type used by the CSD
 * framework.
 */
@Named
@Referenced(type=ReferenceType.ROLE)
public interface RoleDescriptor
    extends AbstractRoleDescriptor {

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
   * Check if this role is Java based.
   */
  boolean isJvmBased();

  /**
   * Any additional external links that are
   * relevant to the role.
   */
  @Valid
  @UniqueField("name")
  List<RoleExternalLink> getAdditionalExternalLinks();

  @NotNull
  @Valid
  RunnerDescriptor getStartRunner();

  /**
   * Allows to override the default stop behavior for a role.
   * If not specified, the process will receive a TERM signal;
   * after a hardcoded timeout we send a group sigkill
   * if the process is still running.
   */
  @Valid
  GracefulStopRoleDescriptor getStopRunner();

  @Valid
  TopologyDescriptor getTopology();

  @AutoConfigSharesValid
  @UniqueField.List({
    @UniqueField("name"),
    @UniqueField("configName")
  })
  @Valid
  List<BasicParameter<?>> getParameters();

  @Valid
  RunAs getRunAs();

  @Valid
  LoggingDescriptor getLogging();

  @UniqueField("name")
  @Valid
  List<RoleCommandDescriptor> getCommands();

  @Valid
  ConfigWriter getConfigWriter();

  @Valid
  CgroupDescriptor getCgroup();

  /**
   * List of kerberos principals used by the role.
   * If this is specified, a keytab file containing all the principals
   * will be added to role's configuration whenever the role
   * is started or when a command is run on the role.
   */
  @Valid
  @UniqueField("name")
  List<KerberosPrincipalDescriptor> getKerberosPrincipals();

  /** Optional. If configured, then indicates that role is an SSL Server. */
  @Valid
  SslServerDescriptor getSslServer();

  /** Optional. If configured, then indicates that role is an SSL Client. */
  @Valid
  SslClientDescriptor getSslClient();

  /**
   * Can be used to indicate which String parameters are unique identifiers for the
   * role. If specified, Cloudera Manager will initialize the parameters to a unique
   * value at role creation.
   */
  public List<String> getUniqueIdParameters();
}
