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
package com.cloudera.csd.validation;

import com.cloudera.csd.components.JsonMdlParser;
import com.cloudera.csd.components.JsonSdlObjectMapper;
import com.cloudera.csd.components.JsonSdlParser;
import com.cloudera.csd.descriptors.RoleDescriptor;
import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.validation.DescriptorValidator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class SdlTestUtils {

  public static final JsonSdlObjectMapper OBJECT_MAPPER;
  public static final JsonSdlParser SDL_PARSER;
  public static final JsonMdlParser MDL_PARSER;
  public static final ServiceDescriptor FULL_DESCRIPTOR;
  public static final DescriptorValidator<ServiceDescriptor>
    FAKE_SDL_VALIDATOR;
  public static final DescriptorValidator<ServiceMonitoringDefinitionsDescriptor>
    FAKE_MDL_VALIDATOR;

  private static final String RESOURCE_PATH = "/com/cloudera/csd/";
  public static final String SDL_VALIDATOR_RESOURCE_PATH = RESOURCE_PATH + "validator/";
  public static final String SDL_REFERENCE_VALIDATOR_RESOURCE_PATH = SDL_VALIDATOR_RESOURCE_PATH + "references/";
  public static final String SDL_PARSER_RESOURCE_PATH = RESOURCE_PATH + "parser/";

  // Initialize our variables
  static {
    OBJECT_MAPPER = new JsonSdlObjectMapper();
    SDL_PARSER = new JsonSdlParser(OBJECT_MAPPER);
    MDL_PARSER = new JsonMdlParser(OBJECT_MAPPER);
    FULL_DESCRIPTOR = getParserSdl("service_full.sdl");
    FAKE_SDL_VALIDATOR = getAlwaysPassingSdlValidator();
    FAKE_MDL_VALIDATOR = getAlwaysPassingMdlValidator();
  }

  public static ServiceDescriptor parseSDL(String path) {
    try {
      InputStream stream = SdlTestUtils.class.getResourceAsStream(path);
      return SDL_PARSER.parse(IOUtils.toByteArray(stream));
    } catch (IOException io) {
      throw new RuntimeException(io);
    }
  }

  public static ServiceMonitoringDefinitionsDescriptor parseMDL(String path) {
    try {
      InputStream stream = SdlTestUtils.class.getResourceAsStream(path);
      return MDL_PARSER.parse(IOUtils.toByteArray(stream));
    } catch (IOException io) {
      throw new RuntimeException(io);
    }
  }

  public static ServiceDescriptor getParserSdl(String filename) {
    return parseSDL(SDL_PARSER_RESOURCE_PATH + filename);
  }

  public static ServiceDescriptor getValidatorSdl(String filename) {
    return parseSDL(SDL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static ServiceMonitoringDefinitionsDescriptor getValidatorMdl(
      String filename) {
    return parseMDL(SDL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static ServiceDescriptor getReferenceValidatorSdl(String filename) {
    return parseSDL(SDL_REFERENCE_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static DescriptorValidator<ServiceDescriptor>
      getAlwaysPassingSdlValidator() {
    return new DescriptorValidator<ServiceDescriptor>() {
      public Set<String> validate(ServiceDescriptor serviceDescriptor) {
        return Sets.newHashSet();
      }
    };
  }

  public static DescriptorValidator<ServiceMonitoringDefinitionsDescriptor>
      getAlwaysPassingMdlValidator() {
    return new DescriptorValidator<ServiceMonitoringDefinitionsDescriptor>() {
      public Set<String> validate(
          ServiceMonitoringDefinitionsDescriptor serviceDescriptor) {
        return Sets.newHashSet();
      }
    };
  }

  public static Map<String, RoleDescriptor> makeRoleMap(Collection<RoleDescriptor> roles) {
    ImmutableMap.Builder<String, RoleDescriptor> builder = ImmutableMap.builder();
    for (RoleDescriptor r : roles) {
      builder.put(r.getName(), r);
    }
    return builder.build();
  }
}
