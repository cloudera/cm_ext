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
 * Indicates that this is an SSL server.
 */
public interface SslServerDescriptor {

  /**
   * Optional. Format of the keystore. If null, use JKS format by default.
   */
  CertificateFileFormat getKeystoreFormat();

  /**
   * Optional. Determine whether to expose the ssl_enabled parameter. When
   * set to "optional" (the default), then ssl_enabled is generated as a
   * boolean parameter with a default of false. When set to "not_exposed" or
   * "required", there is no ssl_enabled parameter and CM will assume that
   * SSL is always enabled for this role.
   */
  CsdParameterOptionality getEnabledOptionality();

  /**
   * Optional. Config name to emit when ssl_enabled is used in a config file.
   * If null, ssl_enabled will not be emitted into config files, and can only
   * be used in substitutions like ${ssl_enabled}.
   */
  String getEnabledConfigName();

  @Referenced(type = ReferenceType.PARAMETER, as = {"ssl_enabled",
      "ssl_server_keystore_location", "ssl_server_keystore_password",
      // TODO we should only conditionally allow references to key password
      "ssl_server_keystore_keypassword" })
  public interface JksSslServerDescriptor extends SslServerDescriptor {
    // fields that only apply to JKS format

    /**
     * Optional. Config name to emit when ssl_server_keystore_location is used in
     * a config file. If null, ssl_server_keystore_location will not be emitted
     * into config files, and can only be used in substitutions like
     * ${ssl_server_keystore_location}.
     */
    String getKeystoreLocationConfigName();

    /**
     * Optional. Default value for ssl_server_keystore_location.
     */
    String getKeystoreLocationDefault();

    /**
     * Optional. Config name to emit when ssl_server_keystore_password is used in
     * a config file. If null, ssl_server_keystore_password will not be emitted
     * into config files, and can only be used in substitutions like
     * ${ssl_server_keystore_password}.
     * <p>
     * You must set this in order to use
     * keystorePasswordCredentialProviderCompatible keystorePasswordScriptBased.
     */
    String getKeystorePasswordConfigName();

    /**
     * Optional. Defaults to false. Whether ssl_server_keystore_password can use
     * the Credential Provider, a Hadoop mechanism that allows for the encrypting
     * of sensitive items in an encrypted store.
     * <p>
     * Has no effect on substitutions like ${ssl_server_keystore_password}
     * <p>
     * Requires keystorePasswordConfigName to be set. Mutually exclusive with
     * keystorePasswordScriptBased.
     */
    boolean isKeystorePasswordCredentialProviderCompatible();

    /**
     * Optional. Defaults to false. If true, the following things happen when used
     * in a configFile (not through substitutions like
     * ${ssl_server_keystore_password}):
     * <ul>
     * <li>1) The regular password for the keystore is no longer emitted</li>
     * <li>2) In its place, CM will emit the full path to a script, and that
     * script will echo the value of this desired password to stdout.</li>
     * </ul>
     * Requires keystorePasswordConfigName to be set. Mutually exclusive with
     * keystorePasswordCredentialProviderCompatible.
     * <p>
     * Has no effect on substitutions like ${ssl_server_keystore_password}.
     * <p>
     * For this functionality to be useful, your code must run the script in the
     * parameter to get the real password.
     */
    boolean isKeystorePasswordScriptBased();

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

    /**
     * Optional. Config name to emit when ssl_server_keystore_keypassword is
     * used in a config file. If null, ssl_server_keystore_keypassword will not
     * be emitted into config files, and can only be used in substitutions like
     * ${ssl_server_keystore_keypassword}.
     * <p>
     * You must set this in order to use keystoreKeyPasswordScriptBased.
     */
    String getKeystoreKeyPasswordConfigName();

    /**
     * Optional. Defaults to false. Whether ssl_server_keystore_keypassword can
     * use the Credential Provider, a Hadoop mechanism that allows for the
     * encrypting of sensitive items in an encrypted store.
     * <p>
     * Has no effect on substitutions like ${ssl_server_keystore_keypassword}
     * <p>
     * Requires keystoreKeyPasswordConfigName to be set. Mutually exclusive with
     * keystoreKeyPasswordScriptBased.
     */
    boolean isKeystoreKeyPasswordCredentialProviderCompatible();

    /**
     * Optional. Defaults to false. If true, the following things happen when
     * used in a configFile (not through substitutions like
     * ${ssl_server_keystore_keypassword}):
     * <ul>
     * <li>1) The regular password for the keystore is no longer emitted</li>
     * <li>2) In its place, CM will emit the full path to a script, and that
     * script will echo the value of this desired password to stdout.</li>
     * </ul>
     * Requires keystoreKeyPasswordConfigName to be set. Mutually exclusive
     * with keystoreKeyPasswordCredentialProviderCompatible.
     * <p>
     * Has no effect on substitutions like ${ssl_server_keystore_keypassword}.
     * <p>
     * For this functionality to be useful, your code must run the script in the
     * parameter to get the real password.
     */
    boolean isKeystoreKeyPasswordScriptBased();
  }

  @Referenced(type = ReferenceType.PARAMETER, as = {"ssl_enabled",
      "ssl_server_privatekey_location", "ssl_server_privatekey_password",
      "ssl_server_certificate_location", "ssl_server_ca_certificate_location"})
  public interface PemSslServerDescriptor extends SslServerDescriptor {
    /**
     * Optional. Config name to emit when ssl_server_privatekey_location is used in
     * a config file. If null, ssl_server_privatekey_location will not be emitted
     * into config files, and can only be used in substitutions like
     * ${ssl_server_privatekey_location}.
     */
    String getPrivateKeyLocationConfigName();

    /**
     * Optional. Default value for ssl_server_privatekey_location.
     */
    String getPrivateKeyLocationDefault();

    /**
     * Optional. Config name to emit when ssl_server_privatekey_password is used
     * in a config file. If null, ssl_server_privatekey_password will not be
     * emitted into config files, and can only be used in substitutions like
     * ${ssl_server_privatekey_password}.
     * <p>
     * You must set this in order to use
     * privateKeyPasswordCredentialProviderCompatible or
     * privateKeyPasswordScriptBased.
     */
    String getPrivateKeyPasswordConfigName();

    /**
     * Optional. Defaults to false. Whether ssl_server_privatekey_password can
     * use the Credential Provider, a Hadoop mechanism that allows for the
     * encrypting of sensitive items in an encrypted store.
     * <p>
     * Has no effect on substitutions like ${ssl_server_privatekey_password}
     * <p>
     * Requires privateKeyPasswordConfigName to be set. Mutually exclusive with
     * privateKeyPasswordScriptBased.
     */
    boolean isPrivateKeyPasswordCredentialProviderCompatible();

    /**
     * Optional. Defaults to false. If true, the following things happen when used
     * in a configFile (not through substitutions like
     * ${ssl_server_privatekey_password}):
     * <ul>
     * <li>1) The regular password for the private key is no longer emitted</li>
     * <li>2) In its place, CM will emit the full path to a script, and that
     * script will echo the value of this desired password to stdout.</li>
     * </ul>
     * Requires privateKeyPasswordConfigName to be set. Mutually exclusive with
     * privateKeyPasswordCredentialProviderCompatible.
     * <p>
     * Has no effect on substitutions like ${ssl_server_privatekey_password}.
     * <p>
     * For this functionality to be useful, your code must run the script in the
     * parameter to get the real password.
     */
    boolean isPrivateKeyPasswordScriptBased();

    /**
     * Optional. Config name to emit when ssl_server_certificate_location is
     * used in a config file. If null, ssl_server_certificate_location will not
     * be emitted into config files, and can only be used in substitutions like
     * ${ssl_server_certificate_location}.
     */
    String getCertificateLocationConfigName();

    /**
     * Optional. Default value for ssl_server_certificate_location.
     */
    String getCertificateLocationDefault();

    /**
     * Optional. Config name to emit when ssl_server_ca_certificate_location is
     * used in a config file. If null, ssl_server_ca_certificate_location will
     * not be emitted into config files, and can only be used in substitutions
     * like ${ssl_server_ca_certificate_location}.
     */
    String getCaCertificateLocationConfigName();

    /**
     * Optional. Default value for ssl_server_ca_certificate_location.
     */
    String getCaCertificateLocationDefault();
  }
}
