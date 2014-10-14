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

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.GROUP;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.HOST;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.PARAMETERS;
import static com.cloudera.csd.validation.references.annotations.SubstitutionType.USER;

import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;
import com.cloudera.csd.validation.references.annotations.IncludeAdditionalReferences;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referencing;

/**
 * Indicates that the service can provide KMS capabilities.
 */
@IncludeAdditionalReferences("roleName")
public interface ProvidesKms {

  /**
   * The role name (type) that of the daemon providing the KMS interface. If
   * load balancer is not specified, this is required.
   *
   * @return role name for KMS provider
   */
  @Referencing(type = ReferenceType.ROLE)
  String getRoleName();

  /**
   * The URL when KMS is not using SSL. Parameters will be evaluated within the
   * context of the KMS role, so you can reference role params as well as
   * service params. Normally this is "http://${host}:${kms_port_param}". When
   * there are multiple KMS roles, load balancer url must be specified and is
   * used instead of this. This is normally required, but can be omitted if a
   * load balancer is always specified or SSL is always enabled.
   *
   * @return insecure URL
   */
  @AvailableSubstitutions(type = { PARAMETERS, HOST, USER, GROUP })
  String getInsecureUrl();

  /**
   * Optional. The full URL of the HTTP load balancer. Required when there are
   * more than one role instances of the KMS-providing role (ie KMS High
   * Availability). Can also be specified when there's just a single role. If
   * this is not set and there are multiple KMS-providing roles assigned to
   * hosts, then this will result in an error. Parameters will be evaluated in
   * the service context, so any referenced parameters must be service-level
   * parameters. When this is specified, it is always used as-is and takes
   * precedence over the insecure and secure URLs.
   *
   * @return full URL of the load balancer
   */
  @AvailableSubstitutions(type = { PARAMETERS, USER, GROUP })
  String getLoadBalancerUrl();

  /**
   * The URL when KMS is using SSL. Parameters will be evaluated within the
   * context of the KMS role, so you can reference role params as well as
   * service params. Normally this is "https://${host}:${kms_ssl_port_param}".
   * When there are multiple KMS roles, load balancer url must be specified and
   * is used instead of this. Only used when SSL is enabled via
   * {@link SslServerDescriptor}.
   *
   * @return insecure URL
   */
  @AvailableSubstitutions(type = { PARAMETERS, HOST, USER, GROUP })
  String getSecureUrl();
}
