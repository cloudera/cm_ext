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

import com.cloudera.csd.descriptors.InterfaceStability.Unstable;
import com.cloudera.csd.descriptors.dependencyExtension.DependencyExtension;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;

import java.util.List;
import javax.validation.Valid;

/**
 * Abstract Descriptor to specify shared functionality for both
 * {@link RoleDescriptor} and {@link GatewayDescriptor}.
 */
public interface AbstractRoleDescriptor {

  /***
   * Optional. If configured, then indicates that role or gateway
   * supports extensions.
   *
   * @return list of extensions.
   */
  @Valid
  @Unstable
  List<DependencyExtension> getDependencyExtensions();
}
