// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.constraints.UniqueServiceType;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

/**
 * The root interface that describes a new service type
 * for the CSD framework.
 */
public interface ServiceDescriptor {

  @EntityTypeFormat
  @UniqueServiceType
  String getName();

  @NotBlank
  String getLabel();

  @NotBlank
  String getDescription();

  @NotBlank
  String getVersion();

  @Valid
  CompatibilityDescriptor getCompatibility();

  @Min(1)
  Integer getMaxInstances();

  @Valid
  RunAs getRunAs();

  String getIcon();

  @Valid
  ParcelDescriptor getParcel();

  /**
   * When set to true, this service participates
   * in the express wizard.
   */
  boolean isInExpressWizard();

  /**
   * The list of roles that have external links
   * that should also be surfaced on the service.
   * It is recommended that this is a list of only
   * singleton/master roles.
   */
  Set<String> getRolesWithExternalLinks();

  @UniqueField("name")
  @Valid
  Set<ServiceCommandDescriptor> getCommands();

  @Valid
  GracefulStopDescriptor getStopRunner();

  @UniqueField("name")
  @Valid
  Set<RoleDescriptor> getRoles();

  @UniqueField.List({
    @UniqueField("name"),
    @UniqueField("configName")
  })
  @Valid
  Set<Parameter<?>> getParameters();
  
  @UniqueField("name")
  @Valid
  Set<ServiceDependency> getServiceDependencies();
  
  @Valid
  GatewayDescriptor getGateway();

  @UniqueField("name")
  @Valid
  Set<CreateHdfsDirDescriptor> getHdfsDirs();
  
  @Valid
  ServiceInitDescriptor getServiceInit();
}
