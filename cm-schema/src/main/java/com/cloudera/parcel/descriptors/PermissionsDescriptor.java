// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Descriptor for the permissions.json file.
 */
public interface PermissionsDescriptor {

  @Valid
  @NotNull
  public Map<String, PermissionDescriptor> getPermissions();
}
