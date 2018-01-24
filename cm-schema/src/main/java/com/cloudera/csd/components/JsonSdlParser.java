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
import com.cloudera.csd.descriptors.PlacementRuleDescriptor.AlwaysWithAnyRule;
import com.cloudera.csd.descriptors.PlacementRuleDescriptor.NeverWithRule;
import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.descriptors.PlacementRuleDescriptor.AlwaysWithRule;
import com.cloudera.csd.descriptors.SslClientDescriptor.JksSslClientDescriptor;
import com.cloudera.csd.descriptors.SslClientDescriptor.PemSslClientDescriptor;
import com.cloudera.csd.descriptors.SslServerDescriptor.JksSslServerDescriptor;
import com.cloudera.csd.descriptors.SslServerDescriptor.PemSslServerDescriptor;
import com.cloudera.csd.descriptors.dependencyExtension.ClassAndConfigsExtension;
import com.cloudera.csd.descriptors.dependencyExtension.DirectoryExtension;
import com.cloudera.csd.descriptors.dependencyExtension.LineageExtension;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.GFlagsGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.HadoopXMLGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator.PropertiesGenerator;
import com.cloudera.csd.descriptors.health.EntityMetricHealthTestDescriptor;
import com.cloudera.csd.descriptors.health.EntityStatusHealthTestDescriptor;
import com.cloudera.csd.descriptors.health.HealthAggregationDescriptor.NonSingletonAggregationDescriptor;
import com.cloudera.csd.descriptors.health.HealthAggregationDescriptor.SingletonAggregationDescriptor;
import com.cloudera.csd.descriptors.parameters.BooleanParameter;
import com.cloudera.csd.descriptors.parameters.DoubleParameter;
import com.cloudera.csd.descriptors.parameters.LongParameter;
import com.cloudera.csd.descriptors.parameters.MemoryParameter;
import com.cloudera.csd.descriptors.parameters.PasswordParameter;
import com.cloudera.csd.descriptors.parameters.PathArrayParameter;
import com.cloudera.csd.descriptors.parameters.PathParameter;
import com.cloudera.csd.descriptors.parameters.PortNumberParameter;
import com.cloudera.csd.descriptors.parameters.ProvidedParameter;
import com.cloudera.csd.descriptors.parameters.StringArrayParameter;
import com.cloudera.csd.descriptors.parameters.StringEnumParameter;
import com.cloudera.csd.descriptors.parameters.StringParameter;
import com.cloudera.csd.descriptors.parameters.URIArrayParameter;
import com.cloudera.csd.descriptors.parameters.URIParameter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * This class is used to read an SDL that is written in the JSON language.
 */
public class JsonSdlParser implements Parser<ServiceDescriptor> {

  private final JsonSdlObjectMapper mapper;

  public JsonSdlParser(JsonSdlObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public ServiceDescriptor parse(byte[] data) throws IOException {
    Preconditions.checkNotNull(data);
    return mapper.readValue(data, ServiceDescriptor.class);
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
      @JsonSubTypes.Type(value = PortNumberParameter.class, name = "port"),
      @JsonSubTypes.Type(value = ProvidedParameter.class, name = "provided")})
  interface ParameterMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "configFormat")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = HadoopXMLGenerator.class, name = "hadoop_xml"),
      @JsonSubTypes.Type(value = PropertiesGenerator.class, name = "properties"),
      @JsonSubTypes.Type(value = GFlagsGenerator.class, name = "gflags")})
  interface GeneratorMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = ClassAndConfigsExtension.class, name = "classAndConfigs"),
      @JsonSubTypes.Type(value = DirectoryExtension.class, name = "directory"),
      @JsonSubTypes.Type(value = LineageExtension.class, name = "lineage")})
  interface DependencyExtensionMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = AlwaysWithRule.class, name = "alwaysWith"),
    @JsonSubTypes.Type(value = AlwaysWithAnyRule.class, name = "alwaysWithAny"),
    @JsonSubTypes.Type(value = NeverWithRule.class, name = "neverWith")})
  interface PlacementRuleMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "keystoreFormat",
      visible = true,
      defaultImpl = JksSslServerDescriptor.class)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = JksSslServerDescriptor.class, name="jks"),
    @JsonSubTypes.Type(value = PemSslServerDescriptor.class, name="pem")
  })
  interface SslServerDescriptorTypeMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "truststoreFormat",
      visible = true,
      defaultImpl = JksSslClientDescriptor.class)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = JksSslClientDescriptor.class, name="jks"),
    @JsonSubTypes.Type(value = PemSslClientDescriptor.class, name="pem")
  })
  interface SslClientDescriptorTypeMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = SingletonAggregationDescriptor.class, name = "singleton"),
      @JsonSubTypes.Type(value = NonSingletonAggregationDescriptor.class, name = "nonSingleton")})
  interface HealthAggregationMixin {
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = EntityStatusHealthTestDescriptor.class, name = "status"),
      @JsonSubTypes.Type(value = EntityMetricHealthTestDescriptor.class, name = "metric")})
  interface HealthTestMixin {
  }
}
