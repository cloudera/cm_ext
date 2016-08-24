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

import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

/**
 * Indicates that this is an SSL client. When specified, the role will
 * automatically get the following parameters: ssl_client_truststore_location,
 * ssl_client_truststore_password (JKS format only)
 */
public interface SslClientDescriptor {

  /**
   * Optional. Format of the truststore. If null, use JKS format by default.
   */
  CertificateFileFormat getTruststoreFormat();

  /**
   * Optional. Config name to emit when ssl_client_truststore_location is used
   * in a config file. If null, ssl_client_truststore_location will not be
   * emitted into config files, and can only be used in substitutions like
   * ${ssl_client_truststore_location}.
   */
  String getTruststoreLocationConfigName();

  /**
   * Optional. Default value for ssl_server_truststore_location.
   */
  String getTruststoreLocationDefault();

  @Referenced(type = ReferenceType.PARAMETER, as = {
      "ssl_client_truststore_location", "ssl_client_truststore_password" })
  public interface JksSslClientDescriptor extends SslClientDescriptor {
    // JKS-specific fields
    /**
     * Optional. Config name to emit when ssl_client_truststore_password is used
     * in a config file. If null, ssl_client_truststore_password will not be
     * emitted into config files, and can only be used in substitutions like
     * ${ssl_client_truststore_password}.
     * <p>
     * You must set this in order to use
     * truststorePasswordCredentialProviderCompatible or
     * truststorePasswordScriptBased.
     */
    String getTruststorePasswordConfigName();

    /**
     * Optional. Defaults to false. Whether ssl_client_truststore_password can
     * use the Credential Provider, a Hadoop mechanism that allows for the
     * encrypting of sensitive items in an encrypted store.
     * <p>
     * Has no effect on substitutions like ${ssl_client_truststore_password}
     * <p>
     * Requires truststorePasswordConfigName to be set. Mutually exclusive with
     * truststorePasswordScriptBased.
     */
    boolean isTruststorePasswordCredentialProviderCompatible();

    /**
     * Optional. Defaults to false. If true, the following things happen when used
     * in a configFile (not through substitutions like
     * ${ssl_client_truststore_password}):
     * <ul>
     * <li>1) The regular password for the truststore is no longer emitted</li>
     * <li>2) In its place, CM will emit the full path to a script, and that
     * script will echo the value of this desired password to stdout.</li>
     * </ul>
     * Requires truststorePasswordConfigName to be set.
     * <p>
     * Has no effect on substitutions like ${ssl_client_truststore_password}.
     * <p>
     * For this functionality to be useful, your code must run the script in the
     * parameter to get the real password.
     */
    boolean isTruststorePasswordScriptBased();
  }

  @Referenced(type = ReferenceType.PARAMETER, as = {
      "ssl_client_truststore_location" })
  public interface PemSslClientDescriptor extends SslClientDescriptor {
    // no PEM-specific fields
  }
}
