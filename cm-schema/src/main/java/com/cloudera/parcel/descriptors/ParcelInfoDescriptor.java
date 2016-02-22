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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.Instant;

/**
 * Descriptor for a single parcel entry in manifest.json
 */
public interface ParcelInfoDescriptor {

  @NotBlank
  String getParcelName();

  @NotBlank
  String getHash();

  @UniqueField("name")
  @Valid
  @NotNull
  List<ComponentDescriptor> getComponents();

  Instant getReleased();

  String getDepends();

  String getReplaces();

  String getConflicts();

  String getReleaseNotes();

  ServicesRestartDescriptor getServicesRestartInfo();
}
