// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
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
}
