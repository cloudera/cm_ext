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
