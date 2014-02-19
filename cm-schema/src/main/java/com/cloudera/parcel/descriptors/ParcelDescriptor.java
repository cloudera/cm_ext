// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
}
