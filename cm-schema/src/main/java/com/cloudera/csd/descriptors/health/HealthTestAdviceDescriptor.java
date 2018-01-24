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

import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referencing;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class HealthTestAdviceDescriptor {

  private String message;
  private List<String> parameters;
  private List<String> commands;

  public HealthTestAdviceDescriptor() {
  }

  /**
   * Parameters relevant to this advice.
   *
   * @return zero or more parameter references
   */
  @Referencing(type = ReferenceType.PARAMETER)
  public List<String> getParameters() {
    return parameters;
  }

  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  /**
   * Commands relevant to this advice.
   *
   * @return zero or more command references
   */
  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(List<String> commands) {
    this.commands = commands;
  }

  /**
   * Helpful message around the purpose of the test, why the test may fail, and
   * possible actions to consider to address health issues.
   *
   * @return advice message
   */
  @NotEmpty
  public String getMessage() {
    return message;
  }

  public void setMessage(String advice) {
    this.message = advice;
  }
}
