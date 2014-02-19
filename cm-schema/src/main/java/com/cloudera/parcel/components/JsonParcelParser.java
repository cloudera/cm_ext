// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import com.cloudera.common.Parser;
import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;


/**
 * This class is used to read a parcel.json file
 */
public class JsonParcelParser extends JsonGenericParser<ParcelDescriptor>
                              implements Parser<ParcelDescriptor> {

  public JsonParcelParser() {
    super(new TypeReference<ParcelDescriptor>() {});
  }

}
