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
package com.cloudera.csd.components;

import com.cloudera.common.Parser;
import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.descriptors.generators.ConfigGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.HadoopXMLGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.PropertiesGenerator;
import com.cloudera.csd.descriptors.parameters.BooleanParameter;
import com.cloudera.csd.descriptors.parameters.DoubleParameter;
import com.cloudera.csd.descriptors.parameters.LongParameter;
import com.cloudera.csd.descriptors.parameters.MemoryParameter;
import com.cloudera.csd.descriptors.parameters.Parameter;
import com.cloudera.csd.descriptors.parameters.PasswordParameter;
import com.cloudera.csd.descriptors.parameters.PathArrayParameter;
import com.cloudera.csd.descriptors.parameters.PathParameter;
import com.cloudera.csd.descriptors.parameters.PortNumberParameter;
import com.cloudera.csd.descriptors.parameters.StringArrayParameter;
import com.cloudera.csd.descriptors.parameters.StringEnumParameter;
import com.cloudera.csd.descriptors.parameters.StringParameter;
import com.cloudera.csd.descriptors.parameters.URIArrayParameter;
import com.cloudera.csd.descriptors.parameters.URIParameter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used to read an SDL that is written in the JSON language.
 */
public class JsonSdlParser implements Parser<ServiceDescriptor> {

  /**
   * We construct a new jackson object mapper for the parser since we want to
   * add some configuration that we don't want to apply to our global object
   * mapper. This object mapper has the following features:
   *
   * 1. Does not fail if there is an unknown element in the json file. It simply
   * ignores the element and continues reading.
   *
   * 2. We use Mr. Bean for bean materialization during deserialization. Mr Bean
   * uses ASM to create classes at runtime that conform to your abstract class
   * or interface. This allows us to define an immutable interface for the
   * service descriptor and not have to write out all the concrete classes.
   *
   * 3. We add mixin classes to the object mapper to let jackson know of
   * property name remaps.
   */
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static {

    final Map<Class<?>, Class<?>> mixins = new HashMap<Class<?>, Class<?>>() {{
      put(Parameter.class, ParameterMixin.class);
      put(ConfigGenerator.class, GeneratorMixin.class);
    }};

    OBJECT_MAPPER.registerModule(new SimpleModule() {
      @Override
      public void setupModule(SetupContext context) {
        for (Entry<Class<?>, Class<?>> entry : mixins.entrySet()) {
          context.setMixInAnnotations(entry.getKey(), entry.getValue());
        }
      }
    });
    OBJECT_MAPPER.registerModule(new MrBeanModule());
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false);
    OBJECT_MAPPER.configure(Feature.ALLOW_COMMENTS, true);
  }

  @Override
  public ServiceDescriptor parse(byte[] data) throws IOException {
    Preconditions.checkNotNull(data);
    return OBJECT_MAPPER.readValue(data, ServiceDescriptor.class);
  }
  
  @JsonTypeInfo(  
      use = JsonTypeInfo.Id.NAME,  
      include = JsonTypeInfo.As.PROPERTY,
      property = "type")  
  @JsonSubTypes({  
      @JsonSubTypes.Type(value = StringParameter.class, name = "string"),
      @JsonSubTypes.Type(value = MemoryParameter.class, name = "memory"),
      @JsonSubTypes.Type(value = LongParameter.class, name = "long"),
      @JsonSubTypes.Type(value = BooleanParameter.class, name = "boolean"),
      @JsonSubTypes.Type(value = DoubleParameter.class, name = "double"),
      @JsonSubTypes.Type(value = PathArrayParameter.class, name = "path_array"),
      @JsonSubTypes.Type(value = StringArrayParameter.class, name = "string_array"),
      @JsonSubTypes.Type(value = StringEnumParameter.class, name = "string_enum"),
      @JsonSubTypes.Type(value = URIArrayParameter.class, name = "uri_array"),
      @JsonSubTypes.Type(value = URIParameter.class, name = "uri"),
      @JsonSubTypes.Type(value = PathParameter.class, name = "path"),
      @JsonSubTypes.Type(value = PasswordParameter.class, name = "password"),
      @JsonSubTypes.Type(value = PortNumberParameter.class, name = "port")})
  interface ParameterMixin {
  }
  
  @JsonTypeInfo(  
      use = JsonTypeInfo.Id.NAME,  
      include = JsonTypeInfo.As.PROPERTY,
      property = "configFormat")  
  @JsonSubTypes({  
      @JsonSubTypes.Type(value = HadoopXMLGenerator.class, name = "hadoop_xml"),
      @JsonSubTypes.Type(value = PropertiesGenerator.class, name = "properties")})
  interface GeneratorMixin {
  }
}
