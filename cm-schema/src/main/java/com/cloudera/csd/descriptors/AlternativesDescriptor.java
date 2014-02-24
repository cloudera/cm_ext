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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Describes an entry for installation with the 'alternatives' mechanism.
 */
public interface AlternativesDescriptor {

  /**
   * The logical name for the link group in alternatives. It will also serve as
   * the sub directory name within the process directory for all the generated
   * configuration files as defined by {@link #getConfigWriter()}.
   */
  @NotNull
  String getName();

  /**
   * The symbolic link to be used by clients that internally points to
   * the alternatives managed locations. The files will be deployed to a
   * subdirectory called "conf". For example, if link root is "/etc/service",
   * the complete link would be "/etc/service/conf".
   */
  @NotNull
  String getLinkRoot();

  /**
   * Default priority when the configuration directory is installed into
   * alternatives.
   *
   * @return the priority, larger value means higher precedence
   */
  @Min(0)
  long getPriority();
}
