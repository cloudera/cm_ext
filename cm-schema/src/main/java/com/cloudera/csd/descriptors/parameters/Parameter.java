// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import org.hibernate.validator.constraints.NotBlank;

/** Interface to specify ParamSpecs in a Service Descriptor. */
public interface  Parameter<T> {
  /**
   * Key for storing the value of this parameter in database.
   * Also used for referencing this parameter in config files (REQUIRED).
   */
  @NotBlank
  String getName();

  /**
   * Display name of this parameter (REQUIRED).
   */
  @NotBlank
  String getLabel();

  /**
   * Description of this parameter (REQUIRED).
   */
  @NotBlank
  String getDescription();

  /**
   * Used as the property name of this parameter while emitting
   * it in config files. If not specified, the {@link #getName()}
   * is used.
   */
  String getConfigName();

  /**
   * Whether this parameter must have a value (DEFAULT: false).
   */
  boolean isRequired();

  /**
   * The default value of this parameter.
   * REQUIRED if the parameter is required.
   */
  T getDefault();
  
  /**
   * Whether the user is allowed to configure this Parameter
   * in Add Service Wizard. (DEFAULT: false)
   */
  boolean isConfigurableInWizard();
}
