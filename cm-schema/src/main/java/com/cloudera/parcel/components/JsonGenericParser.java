// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import com.cloudera.common.Parser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * Base class for parcel json parsers.
 *
 * Provides common functionality for all parsers.
 */
public class JsonGenericParser<T> implements Parser<T> {

  protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static {
    OBJECT_MAPPER.registerModule(new MrBeanModule());
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            true);
    // The CM agent doesn't accept comments when it reads the json
    OBJECT_MAPPER.configure(Feature.ALLOW_COMMENTS, false);
  }

  private final TypeReference<? extends T> typeReference;

  public JsonGenericParser(TypeReference<? extends T> typeReference) {
    this.typeReference = typeReference;
  }

  @Override
  public T parse(byte[] data) throws IOException {
    Preconditions.checkNotNull(data);
    return OBJECT_MAPPER.readValue(data, typeReference);
  }

}
