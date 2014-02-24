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
package com.cloudera.csd.descriptors.generators;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;

/** Descriptor to specify a {@link ConfigFileGenerator}. */
public interface ConfigGenerator {

  @NotBlank
  String getFilename();
  
  Set<String> getIncludedParams();
  
  Set<String> getExcludedParams();
  
  // These subclasses don't have any fields yet,
  // but we want to be consistent with how Parameters are defined.
  public interface HadoopXMLGenerator extends ConfigGenerator {
  }
  
  public interface PropertiesGenerator extends ConfigGenerator {
  }
}
