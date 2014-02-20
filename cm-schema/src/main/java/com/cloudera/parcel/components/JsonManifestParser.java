// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import com.cloudera.common.Parser;
import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;


/**
 * This class is used to read a manifest.json file
 */
public class JsonManifestParser extends JsonGenericParser<ManifestDescriptor>
    implements Parser<ManifestDescriptor> {

  public JsonManifestParser() {
    super(new TypeReference<ManifestDescriptor>() {});
  }

}
