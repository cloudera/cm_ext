// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.Instant;

/**
 * The root descriptor for the manifest.json file.
 */
public interface ManifestDescriptor {

  @NotNull
  Instant getLastUpdated();

  @UniqueField("parcelName")
  @Valid
  @NotNull
  List<ParcelInfoDescriptor> getParcels();
}
