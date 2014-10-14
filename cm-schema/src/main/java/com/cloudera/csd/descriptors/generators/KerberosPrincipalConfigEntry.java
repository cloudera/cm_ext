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

import com.cloudera.csd.descriptors.ServiceDescriptor;
import org.hibernate.validator.constraints.NotBlank;

/** Descriptor to specify an entry in config file referencing a kerberos principal. */
public interface KerberosPrincipalConfigEntry {

  /**
   * Whether the principal is specified in
   * {@link ServiceDescriptor#getExternalKerberosPrincipals()}.
   */
  boolean isExternal();

  /**
   * Whether the principal belongs to a different role type. If this is
   * set, principal from an arbitrary role of that role type is used.
   * If both external and peerRoleType are specified, external takes precedence. 
   */
  String getPeerRoleType();

  /**
   * Name of the principal to be emitted in config file.
   */
  @NotBlank
  String getPrincipalName();

  /**
   * Property name to be used while emitting the principal in config file.
   */
  @NotBlank
  String getPropertyName();

  /**
   * Optional wildcard string that will be used to replace the instance
   * part of the principal while emitting it in a config file.
   * E.g. hdfs/${host}@REALM will be emitted as hdfs/_HOST@REALM
   * if instance wildcard is "_HOST".
   */
  String getInstanceWildcard();
}
