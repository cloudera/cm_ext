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

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.PARAMETERS;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.HOST;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.GROUP;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.USER;

import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a url that can point to an external
 * web server that will give additional status information
 * about the role.
 */
public interface RoleExternalLink {

  /**
   * The logical name for this link
   */
  @NotBlank
  String getName();

  /**
   * User friendly name for this link.
   */
  @NotBlank
  String getLabel();

  /**
   * The URL to the external site. This
   * can include all standard substitutions.
   *
   * Eg. http://${host}:${web_port}/status
   * where web_port is a parameter for this role.
   */
  @NotBlank
  @AvailableSubstitutions(type={PARAMETERS, HOST, GROUP, USER})
  String getUrl();

  /**
   * Optional. The URL to the external site when SSL is enabled. SSL is
   * considered enabled when this role has registered
   * {@link RoleDescriptor#getSslServer()} and the cluster administrator has
   * configured SSL to be enabled. If secureUrl is not provided, then will
   * always use the regular url.
   */
  @AvailableSubstitutions(type={PARAMETERS, HOST, GROUP, USER})
  String getSecureUrl();
}
