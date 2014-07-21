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

import com.cloudera.csd.validation.references.annotations.IncludeAdditionalReferences;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.Referencing;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;
import com.fasterxml.jackson.annotation.JsonValue;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a service command used by the CSD framework.
 */
@Named
@Referenced(type=ReferenceType.SERVICE_COMMAND)
@IncludeAdditionalReferences("roleName")
public interface ServiceCommandDescriptor {

  enum RunMode {
    /**
     * Run the role command on all roles of the applicable role type.
     */
    ALL,
    /**
     * Run the role command on one arbitrarily chosen role of the role type.
     */
    SINGLE;

    @JsonValue
    public String toJson() {
      return name().toLowerCase();
    }
  }

  @NotBlank
  String getName();

  @NotBlank
  String getLabel();

  @NotBlank
  String getDescription();

  @NotBlank
  @Referencing(type = ReferenceType.ROLE)
  String getRoleName();

  @NotBlank
  @Referencing(type = ReferenceType.ROLE_COMMAND)
  String getRoleCommand();

  RunMode getRunMode();
}
