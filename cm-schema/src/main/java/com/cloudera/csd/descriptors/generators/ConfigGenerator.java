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
package com.cloudera.csd.descriptors.generators;

import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.Referencing;
import com.cloudera.csd.validation.references.annotations.ReferenceType;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

/** Descriptor to specify a {@link ConfigFileGenerator}. */
@Named("filename")
public interface ConfigGenerator {

  @NotBlank
  String getFilename();

  /**
   * Optional. Whether this file can be refreshed. If at least one file is
   * refreshable, the role will have a refresh command automatically created for
   * it that will refresh all files for the role.
   * <p>
   * Defaults to False.
   */
  boolean isRefreshable();

  @Referencing(type=ReferenceType.PARAMETER)
  Set<String> getIncludedParams();

  @Referencing(type=ReferenceType.PARAMETER)
  Set<String> getExcludedParams();

  /** Kerberos principals to emit into config file. */
  @Valid
  List<KerberosPrincipalConfigEntry> getKerberosPrincipals();

  /**
   * Emitted after parameter configs, before safety valves.
   */
  @Valid
  List<ConfigEntry> getAdditionalConfigs();

  // These subclasses don't have any fields yet,
  // but we want to be consistent with how Parameters are defined.
  public interface HadoopXMLGenerator extends ConfigGenerator {
  }
  
  public interface PropertiesGenerator extends ConfigGenerator {
  }

  public interface GFlagsGenerator extends ConfigGenerator {
  }
}
