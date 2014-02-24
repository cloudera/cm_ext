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
package com.cloudera.parcel.components;

import com.cloudera.common.Parser;
import com.cloudera.parcel.descriptors.PermissionDescriptor;
import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * This class is used to read a permissions.json file
 */
public class JsonPermissionsParser extends JsonGenericParser<PermissionsDescriptor>
                                   implements Parser<PermissionsDescriptor> {

  /**
   * This adaptor class is required to transform the generic Map, otherwise returned
   * from deserializing a permissions.json file, into a POJO with the Map stored
   * as a field. This is necessary for JSR303 validation to work. Attempts to
   * validate the Map directly do not validate the values inside it.
   */
  private static class PermissionsDescriptorImpl implements PermissionsDescriptor {
    private final ImmutableMap.Builder<String, PermissionDescriptor> permissions = ImmutableMap.builder();

    @Override
    public Map<String, PermissionDescriptor> getPermissions() {
      return permissions.build();
    }

    @JsonAnySetter
    private void add(String name, PermissionDescriptor alternative) {
      permissions.put(name, alternative);
    }

  }

  public JsonPermissionsParser() {
    super(new TypeReference<PermissionsDescriptorImpl>() {});
  }

}
