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

/**
 * Description of what authority is required to perform various actions. Not
 * intended for use outside of Cloudera, as Authorities are not part of any
 * documentation or stable API.
 */
@Unstable
public interface AuthorityDescriptor {

  /**
   * Optional. Which authority is required for adding or removing.
   */
  @Unstable
  String getAuthorityForAddRemove();

  /**
   * Optional. The default authority for Parameters.
   */
  @Unstable
  String getDefaultAuthorityForParameters();

  /**
   * Optional. Which authority is required for start/stop.
   */
  @Unstable
  String getAuthorityForPowerState();
}
