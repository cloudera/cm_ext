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
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

import org.hibernate.validator.constraints.NotBlank;


/**
 * Indicates that this is an SSL server. Currently only supports the JKS format.
 * When included, the role will automatically get the following parameters:
 * ssl_enabled, ssl_server_keystore_location, ssl_server_keystore_password.
 * Depending on the value of keyPasswordOptionality, the role will also get
 * ssl_server_keystore_keypassword.
 */
@Referenced(type = ReferenceType.PARAMETER, as = { "ssl_enabled",
    "ssl_server_keystore_location", "ssl_server_keystore_password",
    // TODO we should only conditionally allow references to key password
    "ssl_server_keystore_keypassword" })
public interface SslServerDescriptor {

  /**
   * The identifier for this role's key in the keystore.
   */
  @NotBlank
  @AvailableSubstitutions(type = { PARAMETERS, HOST, USER, GROUP })
  String getKeyIdentifier();

  /**
   * Optional. Whether to allow and / or require the cluster administrator to
   * configure a password for the SSL server's key in the keystore, exposed as
   * parameter ssl_server_keystore_keypassword.
   *
   * @return parameter optionality, or null to indicate NOT_EXPOSED.
   */
  CsdParameterOptionality getKeyPasswordOptionality();
}
