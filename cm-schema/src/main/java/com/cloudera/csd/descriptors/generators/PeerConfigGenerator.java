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

import com.cloudera.csd.validation.references.annotations.IncludeAdditionalReferences;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.Referencing;
import com.cloudera.csd.validation.references.annotations.ReferenceType;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/** a descriptor to get peer parameter configurations **/
@Named("filename")
@IncludeAdditionalReferences("roleName")
public interface PeerConfigGenerator {

  /** The file name of the config file that will be written **/
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

  /** The parameters to include from each peer **/
  @NotEmpty
  @Referencing(type=ReferenceType.PARAMETER)
  Set<String> getParams();

  /** specifies which role type to use, be default it uses the current role type **/
  @Referencing(type=ReferenceType.ROLE)
  String getRoleName();
}
