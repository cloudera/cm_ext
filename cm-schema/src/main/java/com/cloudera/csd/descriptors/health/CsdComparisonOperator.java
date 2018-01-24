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
package com.cloudera.csd.descriptors.health;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;

public enum CsdComparisonOperator {

  /**
   * Less than, i.e. <
   */
  LT("<"),
  /**
   * Less than or equal to, i.e. <=
   */
  LTE("<="),
  /**
   * Greater than, i.e. >
   */
  GT(">"),
  /**
   * Greater than or equal to, i.e. >=
   */
  GTE(">="),
  /**
   * Equal to, i.e. =
   */
  EQ("=="),
  /**
   * Not equal to, i.e. !=
   */
  NEQ("!=");

  private CsdComparisonOperator(String op) {
  }


  @JsonValue
  public String toJson() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
  }
}
