// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import com.cloudera.validation.constraints.FilePermission;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Descriptor for a single entry in the permissions.json file.
 */
public interface PermissionDescriptor {
  @NotBlank
  String getUser();

  @NotBlank
  String getGroup();

  @FilePermission
  String getPermissions();
}
