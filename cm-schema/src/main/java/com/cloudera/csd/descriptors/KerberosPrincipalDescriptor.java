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

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.HOST;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.PARAMETERS;

import org.hibernate.validator.constraints.NotBlank;

import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;

/**
 * Describes a kerberos principal used by a role.
 * A kerberos principal is of the form primary/instance@REALM.
 */
public interface KerberosPrincipalDescriptor {

  /**
   * The name of the principal. The kerberos principal is added to
   * the role's environment with this key if role requires kerberos authentication.
   */
  @NotBlank
  String getName();

  /**
   * First part of the principal.
   */
  @NotBlank
  String getPrimary();

  /**
   * Second part of the principal. If this is a URI, will extract the host name
   * and use that as the instance name. If omitted, then the principal will just
   * be primary@REALM.
   */
  @AvailableSubstitutions(type={PARAMETERS, HOST})
  String getInstance();
}
