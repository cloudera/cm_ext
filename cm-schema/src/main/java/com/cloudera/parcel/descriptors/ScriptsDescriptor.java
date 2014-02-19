// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

/**
 * Descriptor for the 'scripts' section of parcel.json
 *
 * The scripts section can either be empty, or contain excatly one entry,
 * the 'defines' script.
 */
public interface ScriptsDescriptor {
  String getDefines();
}
