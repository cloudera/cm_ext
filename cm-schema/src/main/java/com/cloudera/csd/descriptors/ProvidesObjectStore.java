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

import com.cloudera.csd.descriptors.generators.ConfigGenerator;

import java.util.List;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Indicates that the service can provide object store capabilities,
 * and can act as a secondary DFS.
 */
@InterfaceStability.Unstable
public interface ProvidesObjectStore {

  /**
   * Used to determine how selection and mapping of the external account
   * credentials is performed.
   * TODO make mapping logic account-type agnostic, shift to CSD author
   */
  @NotEmpty
  List<CsdExternalAccountType> getAccountTypes();

  /**
   * This generator allows authors specify configuration that will be
   * merged into core-site.xml
   * (Filename and configFormat are irrelevant)
   */
  @Valid
  ConfigGenerator getCoreSiteGenerator();

  /**
   * Enables different security constraints and operating modes of
   * the connector for the distribution of secret keys.
   * Implementation-dependent, this is in use for S3
   */
  boolean isSupportsKeyDistributionPolicy();
}
