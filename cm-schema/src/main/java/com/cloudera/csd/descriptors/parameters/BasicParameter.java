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

import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Minimal interface to specify ParamSpecs in a CSD descriptor.
 */
@Named
@Referenced(type= ReferenceType.PARAMETER)
public interface BasicParameter<T> {

  /**
   * Key for storing the value of this parameter in database.
   * Also used for referencing this parameter in config files (REQUIRED).
   */
  @NotBlank
  String getName();
}
