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
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * This class is used to read an MDL that is written in the JSON language.
 */
@SuppressWarnings("serial")
public class JsonMdlParser
  implements Parser<ServiceMonitoringDefinitionsDescriptor> {

  // We deserialize using a MrBean based ObjectMapper. When serializing, we use
  // our own ObjectMapper that has a filter to leave out the properties put in
  // place by MrBean that start with a _. If we don't do this, we end up with
  // two copies of each property, e.g. "name" and "_name".
  static final ObjectWriter OBJECT_WRITER;
  static final ObjectWriter PRETTY_OBJECT_WRITER;
  static {
    SimpleBeanPropertyFilter filter = new SimpleBeanPropertyFilter() {
      @Override
      protected boolean include(BeanPropertyWriter writer) {
        return !writer.getName().startsWith("_");
      }
    };
    final String filterName = "exclude-properties-starting-with-underscores";
    SimpleFilterProvider filters =
        new SimpleFilterProvider().addFilter(filterName, filter);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      @Override
      public Object findFilterId(AnnotatedClass a) {
        return filterName;
      }
    });
    OBJECT_WRITER = mapper.writer(filters);
    PRETTY_OBJECT_WRITER = OBJECT_WRITER.withDefaultPrettyPrinter();
  }

  @Override
  public ServiceMonitoringDefinitionsDescriptor parse(
      byte[] data) throws IOException {
    Preconditions.checkNotNull(data);
    return JsonSdlParser.OBJECT_MAPPER.readValue(
        data, ServiceMonitoringDefinitionsDescriptor.class);
  }

  /**
   * Convert the input descriptor into a string. This method should be used in
   * place of JsonUtil since it hides implicit properties introduced by MrBean.
   * @param descriptor
   * @param usePrettyFormat
   * @return
   * @throws IOException
   */
  public String valueAsString(
      ServiceMonitoringDefinitionsDescriptor descriptor,
      boolean usePrettyFormat) throws IOException {
    if (usePrettyFormat) {
      return PRETTY_OBJECT_WRITER.writeValueAsString(descriptor);
    } else {
      return OBJECT_WRITER.writeValueAsString(descriptor);
    }
  }
}
