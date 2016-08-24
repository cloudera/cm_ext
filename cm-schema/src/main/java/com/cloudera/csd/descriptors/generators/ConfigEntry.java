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
package com.cloudera.csd.descriptors.generators;

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.*;

import com.cloudera.csd.descriptors.CsdConfigEntryType;
import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;

import org.hibernate.validator.constraints.NotBlank;

/** Descriptor to specify an entry in a config file */
public interface ConfigEntry {

  @NotBlank
  @AvailableSubstitutions(type={PARAMETERS, HOST, GROUP, USER})
  String getKey();

  @AvailableSubstitutions(type={PARAMETERS, HOST, GROUP, USER})
  String getValue();

  /** Type of the entry. If none is specified, it defaults to "simple". */
  CsdConfigEntryType getType();
}
