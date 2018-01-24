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
package com.cloudera.csd.validation.references.annotations;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * The types of substitutions. Each type can support one or more variable names
 * to be dynamically substituted at runtime.
 */
public enum SubstitutionType {

  /**
   * Parameters depending on the context. {@link #getVariables()} not applicable
   * since they can only be determined dynamically.
   */
  PARAMETERS() {
    @Override
    public List<String> getVariables() {
      throw new IllegalStateException("getVariables() not valid for PARAMETERS");
    }
  },
  HOST,
  USER,
  GROUP,
  PRINCIPAL,
  HEALTH(ImmutableList.of("status.count", "status.message", "metric.value"));

  private List<String> vars;

  SubstitutionType() {
    this.vars = ImmutableList.of(name().toLowerCase());
  }

  SubstitutionType(List<String> vars) {
    this.vars = vars;
  }

  /**
   * Get the list of all variables supported by this substitution type. One
   * exception is {@link #PARAMETERS}, whose variables can only be dynamically
   * determined.
   *
   * @return List of supported variables.
   */
  public List<String> getVariables() {
    return vars;
  }
}
