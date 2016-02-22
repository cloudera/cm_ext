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
package com.cloudera.parcel.descriptors;

import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * The root descriptor for the parcel.json file
 */
public interface ParcelDescriptor {

  @NotNull
  @Range(min = 1, max = 1)
  Integer getSchema_version();

  @NotBlank
  @Pattern(regexp="^((?!-).)*$", message="{custom.validation.constraints.ParcelName.message}")
  String getName();

  @NotBlank
  String getVersion();

  Map<String, String> getExtraVersionInfo();

  @NotNull
  Boolean getSetActiveSymlink();

  String getDepends();

  String getReplaces();

  String getConflicts();

  Set<String> getProvides();

  @Valid
  @NotNull
  ScriptsDescriptor getScripts();

  @UniqueField("name")
  @Valid
  @NotNull
  Set<PackageDescriptor> getPackages();

  @UniqueField("name")
  @Valid
  @NotNull
  Set<ComponentDescriptor> getComponents();

  @Valid
  @NotNull
  Map<String, UserDescriptor> getUsers();

  @NotNull
  Set<String> getGroups();

  ServicesRestartDescriptor getServicesRestartInfo();
}
