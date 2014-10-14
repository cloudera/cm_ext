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

import static com.cloudera.csd.validation.references.annotations.SubstitutionType.*;

import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;
import com.cloudera.validation.constraints.FilePermission;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/** Interface to specify command to create directory in HDFS. */
@Named
@Referenced(type=ReferenceType.SERVICE_COMMAND)
public interface CreateHdfsDirDescriptor {
  
  /** Name of the command. */
  @NotBlank
  String getName();
  
  /** Display name of the command. */
  @NotBlank
  String getLabel();
  
  /** Help string for the command. */
  @NotBlank
  String getDescription();

  /** Description for this directory. */
  @NotBlank
  String getDirectoryDescription();

  /** The path in HDFS to create. */
  @NotBlank
  @AvailableSubstitutions(type={PARAMETERS, USER, GROUP})
  String getPath();

  /** Permission for this directory. */
  @NotNull
  @FilePermission
  String getPermissions();
}
