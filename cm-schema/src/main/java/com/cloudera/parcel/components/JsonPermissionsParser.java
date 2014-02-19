// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
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
