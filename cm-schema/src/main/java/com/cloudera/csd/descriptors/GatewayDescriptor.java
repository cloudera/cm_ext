// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Describes a gateway for a service, including configurations and how
 * to package and deploy them. The output configurations are deployed onto
 * the client host using the Linux "alternatives" mechanism.
 */
public interface GatewayDescriptor {

  /**
   * Describes how to install the client configuration into the 'alternatives'
   * mechanism.
   */
  @NotNull
  @Valid
  AlternativesDescriptor getAlternatives();

  /**
   * Optional script to run in the same directory as
   * {@link #getAlternativesName()}.
   *
   * @return the runner
   */
  @Valid
  RunnerDescriptor getScriptRunner();

  @UniqueField.List({
    @UniqueField("name"),
    @UniqueField("configName")
  })
  @Valid
  Set<Parameter<?>> getParameters();

  @NotNull
  @Valid
  ConfigWriter getConfigWriter();
}
