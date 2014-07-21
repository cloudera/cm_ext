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
package com.cloudera.csd.descriptors;

import com.cloudera.csd.descriptors.generators.AuxConfigGenerator;
import com.cloudera.csd.descriptors.generators.ConfigGenerator;
import com.cloudera.csd.descriptors.generators.PeerConfigGenerator;
import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.List;

import javax.validation.Valid;

public interface ConfigWriter {

  @UniqueField("filename")
  @Valid
  List<AuxConfigGenerator> getAuxConfigGenerators();

  @UniqueField("filename")
  @Valid
  List<ConfigGenerator> getGenerators();

  @UniqueField("filename")
  @Valid
  List<PeerConfigGenerator> getPeerConfigGenerators();
}
