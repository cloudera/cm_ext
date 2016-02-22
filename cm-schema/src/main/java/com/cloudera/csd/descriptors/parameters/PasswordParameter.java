// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import com.cloudera.csd.validation.constraints.Expression;

/**
 * Used to indicate a parameter is a password.
 */
@Expression("credentialProviderCompatible == false or alternateScriptParameterName == null")
public interface PasswordParameter extends StringParameter {

  /**
   * Whether this parameter can use the Credential Provider, a Hadoop
   * mechanism that allows for the encrypting of sensitive items in
   * an encrypted store.
   *
   * This is mutually exclusive with alternateScriptParameterName.
   */
  boolean isCredentialProviderCompatible();

  /**
   * If "alternateScriptParameterName" is specified for this password
   * parameter, the following things happen:
   *
   * 1) The "configName" of this parameter is no longer emitted
   * 2) In its place, a parameter with the name specified by the
   *    "alternateScriptParameterName" is emitted. This parameter contains
   *    the full path to a script, and that script will echo the value
   *    of this desired password to stdout. This only affects when this
   *    parameter is used normally in a config file, not through
   *    substitutions like ${paramter_name}. Substitutions will always get
   *    the raw password.
   *
   * For this functionality to be useful, your code must accept the
   * parameter specified in "alternateScriptParameterName" as a parameter
   * that replaces the "configName" and is known to point to the full path
   * of a script that will print the desired password to stdout.
   *
   * This is mutually exclusive with credentialProviderCompatible.
   */
  String getAlternateScriptParameterName();
}
