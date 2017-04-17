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
package com.cloudera.csd.descriptors.parameters;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Interface to specify ParamSpecs in a CSD descriptor.
 */
public interface Parameter<T> extends BasicParameter<T> {

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

  /**
   * Whether the data in this parameter is sensitive.
   */
  boolean isSensitive();
}
