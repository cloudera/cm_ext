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
package com.cloudera.csd.tools;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.StringUtils;

/**
 * Serializing and deserializing JSON data helper functions using the
 * fasterxml library.
 */
public class JsonUtil {

  @SuppressWarnings("serial")
  public static class JsonRuntimeException extends RuntimeException {
    public JsonRuntimeException(Throwable e) {
      super(e);
    }
  }

  private static final ObjectMapper OBJECT_MAPPER;
  private static final ObjectWriter OBJECT_WRITER;

  static {
    OBJECT_MAPPER = createObjectMapper();
    OBJECT_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
  }

  /**
   * Creates a new {@link ObjectMapper} instance with the certain default
   * behavior: (1) sorting properties alphabetically, and (2) support for Joda
   * time format.
   * <p/>
   */
  public static ObjectMapper createObjectMapper() {
    ObjectMapper newMapper = new ObjectMapper();
    newMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    newMapper.registerModule(new JodaModule());
    return newMapper;
  }

  /**
   * Reads 'str' and deserialize it to type T.
   * @param typeRef
   * @param str
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T valueFromString(TypeReference<T> typeRef, String str) {
    try {
      // cast here disables javac's broken type inference
      return (T) OBJECT_MAPPER.readValue(str, typeRef);
    } catch (JsonParseException e) {
      throw new JsonRuntimeException(e);
    } catch (JsonMappingException e) {
      throw new JsonRuntimeException(e);
    } catch (IOException e) {
      throw new JsonRuntimeException(e);
    }
  }

  /**
   * Reads 'str' and deserialize it to an instance of 'clazz'.
   * @param clazz
   * @param str
   * @param <T>
   * @return
   */
  public static <T> T valueFromString(Class<T> clazz, String str) {
    try {
      // cast here disables javac's broken type inference
      return OBJECT_MAPPER.readValue(str, clazz);
    } catch (JsonParseException e) {
      throw new JsonRuntimeException(e);
    } catch (JsonMappingException e) {
      throw new JsonRuntimeException(e);
    } catch (IOException e) {
      throw new JsonRuntimeException(e);
    }
  }

  /**
   * Reads 'is' and deserialize it to an instance of 'clazz'. Note that the
   * caller is responsible to close the InputStream.
   * @param clazz
   * @param is
   * @param <T>
   * @return
   */
  public static <T> T valueFromStream(Class<T> clazz, InputStream is) {
    try {
      return OBJECT_MAPPER.readValue(is, clazz);
    } catch (IOException ioe) {
      throw new JsonRuntimeException(ioe);
    }
  }

  /**
   * Loads JSON data from a stream. This method closes the stream.
   * @param typeReference
   * @param is
   * @param <T>
   * @return
   * @throws com.cloudera.enterprise.JsonUtil2.JsonRuntimeException which is a
   *         RuntimeException on parse errors.
   */

  /**
   * Reads 'is' and deserialize it to type T. Note that the caller is
   * responsible for closing the InputStream.
   * @param typeReference
   * @param is
   * @param <T>
   * @return
   */
  public static <T> T valueFromStream(TypeReference<?> typeReference,
      InputStream is) {
    try {
      return OBJECT_MAPPER.readValue(is, typeReference);
    } catch (IOException ioe) {
      throw new JsonRuntimeException(ioe);
    }
  }

  /**
   * Serializes 'data' into a UTF8 byte array.
   * @param data
   * @return
   */
  public static byte[] valueAsBytes(Object data) {
    return StringUtils.getBytesUtf8(valueAsString(data));
  }

  /**
   * Serializes 'data' into a string.
   * @param data
   * @return
   */
  public static String valueAsString(Object data) {
    return valueAsString(data, /* usePrettyFormat= */false);
  }

  /**
   * Serializes 'data' into a string optionally using pretty format.
   * @param data
   * @param usePrettyFormat
   * @return
   */
  public static String valueAsString(Object data, boolean usePrettyFormat) {
    try {
      if (usePrettyFormat) {
        return OBJECT_WRITER.writeValueAsString(data);
      } else {
        return OBJECT_MAPPER.writeValueAsString(data);
      }
    } catch (JsonGenerationException e) {
      throw new JsonRuntimeException(e);
    } catch (JsonMappingException e) {
      throw new JsonRuntimeException(e);
    } catch (IOException e) {
      throw new JsonRuntimeException(e);
    }
  }
}
