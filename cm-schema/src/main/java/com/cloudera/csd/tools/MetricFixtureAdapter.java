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
package com.cloudera.csd.tools;

import com.cloudera.csd.descriptors.MetricDescriptor;

import java.util.Collection;

import javax.annotation.Nullable;

/**
 * A MetricFixtureAdapter takes a fixture in a format it recognizes and exposes
 * the metrics for the entity types in a given service.
 */
public interface MetricFixtureAdapter {

  public static final String DEFAULT_CONVENTIONS = null;

  /**
   * Initialize the adapter with the fixture and conventions files. The fixture
   * file can be assumed to exist. If the conventions file is non-null, it can
   * be assumed to exist.
   * @param fixtureFile
   * @param conventionsFile
   */
  public void init(String fixtureFile,
                   @Nullable String conventionsFile) throws Exception;

  /**
   * Returns a list of service MetricDescriptors.
   * @return
   */
  public Collection<MetricDescriptor> getServiceMetrics();

  /**
   * Returns a list of MetricDescriptors for the role type with the input name.
   * @param roleName
   * @return
   */
  public Collection<MetricDescriptor> getRoleMetrics(String roleName);

  /**
   * Returns a list of MetricDescriptors for an entity type with the input
   * name.
   * @param entityName
   * @return
   */
  public Collection<MetricDescriptor> getEntityMetrics(String entityName);
}
