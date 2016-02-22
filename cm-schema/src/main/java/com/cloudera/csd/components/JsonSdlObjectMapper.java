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

import com.cloudera.csd.components.JsonSdlParser.DependencyExtensionMixin;
import com.cloudera.csd.components.JsonSdlParser.GeneratorMixin;
import com.cloudera.csd.components.JsonSdlParser.ParameterMixin;
import com.cloudera.csd.components.JsonSdlParser.PlacementRuleMixin;
import com.cloudera.csd.components.JsonSdlParser.SslClientDescriptorTypeMixin;
import com.cloudera.csd.components.JsonSdlParser.SslServerDescriptorTypeMixin;
import com.cloudera.csd.descriptors.PlacementRuleDescriptor;
import com.cloudera.csd.descriptors.SslClientDescriptor;
import com.cloudera.csd.descriptors.SslServerDescriptor;
import com.cloudera.csd.descriptors.dependencyExtension.DependencyExtension;
import com.cloudera.csd.descriptors.generators.ConfigGenerator;
import com.cloudera.csd.descriptors.parameters.Parameter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class JsonSdlObjectMapper {

  private final ObjectMapper mapper = createObjectMapper();

  /**
   * We construct a new jackson object mapper for the parser since we want to
   * add some configuration that we don't want to apply to our global object
   * mapper. This object mapper has the following features:
   *
   * 1. Does not fail if there is an unknown element in the json file, by default.
   * It simply ignores the element and continues reading. This can be reconfigured.
   *
   * 2. We use Mr. Bean for bean materialization during deserialization. Mr Bean
   * uses ASM to create classes at runtime that conform to your abstract class
   * or interface. This allows us to define an immutable interface for the
   * service descriptor and not have to write out all the concrete classes.
   *
   * 3. We add mixin classes to the object mapper to let jackson know of
   * property name remaps.
   * @return
   */
  private ObjectMapper createObjectMapper() {
    final Map<Class<?>, Class<?>> mixins = new HashMap<Class<?>, Class<?>>() {{
      put(Parameter.class, ParameterMixin.class);
      put(ConfigGenerator.class, GeneratorMixin.class);
      put(DependencyExtension.class, DependencyExtensionMixin.class);
      put(PlacementRuleDescriptor.class, PlacementRuleMixin.class);
      put(SslServerDescriptor.class, SslServerDescriptorTypeMixin.class);
      put(SslClientDescriptor.class, SslClientDescriptorTypeMixin.class);
    }};

    ObjectMapper m = new ObjectMapper();
    m.registerModule(new SimpleModule() {
      @Override
      public void setupModule(SetupContext context) {
        for (Entry<Class<?>, Class<?>> entry : mixins.entrySet()) {
          context.setMixInAnnotations(entry.getKey(), entry.getValue());
        }
      }
    });
    m.registerModule(new MrBeanModule());
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false);
    m.configure(Feature.ALLOW_COMMENTS, true);
    return m;
  }

  public void setFailOnUnknownProperties(boolean fail) {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                     fail);
  }

  public <T> T readValue(byte[] src, Class<T> valueType)
      throws JsonParseException, JsonMappingException, IOException {
    return mapper.readValue(src, valueType);
  }

  public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef)
      throws JsonParseException, JsonMappingException, IOException {
    return mapper.readValue(src, valueTypeRef);
  }
}
