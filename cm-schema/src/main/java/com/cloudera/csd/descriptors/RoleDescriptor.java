// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
