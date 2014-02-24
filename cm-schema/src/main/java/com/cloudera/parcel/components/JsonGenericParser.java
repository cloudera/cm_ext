// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
