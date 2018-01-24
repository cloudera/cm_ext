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
package com.cloudera.csd.descriptors.supportBundle;

import com.cloudera.csd.descriptors.InterfaceStability.Unstable;
import com.cloudera.csd.descriptors.RunnerDescriptor;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Interface to specify the support bundle diagnostics.
 */
@Unstable
public interface SupportBundleDescriptor {
  enum RunMode {
    /**
     * Collect support bundle on all roles of the applicable role type.
     */
    ALL,
    /**
     * Collect support bundle on one arbitrarily chosen role of the role type.
     */
    SINGLE;

    @JsonValue
    public String toJson() {
      return name().toLowerCase();
    }
  }

  /**
   * A short helper message describing what is being collected and why it is
   * useful.
   */
  @NotBlank
  String getDescription();

  /**
   * Run mode for executing the support bundle command on roles belonging
   * to this role type.
   */
  @Nonnull
  RunMode getRunMode();

  /**
   * A timeout value in milliseconds after which the support bundle command
   * times out.
   * <p>
   * Note:
   * <ol>
   * <li> Min value = 1 (1 ms)
   * <li> Max value = 300000 (5 minutes)
   * <li> Default value = 60000 (1 minute)
   * </ol>
   */
  @Min(1)
  @Max(300000)
  Long getTimeout();

  /**
   * The runner that contains all the information required to collect support
   * bundle diagnostics. This runner needs to adhere to the following:
   * <ol>
   * <li>Valid exit code = 0.
   * <li>The runner should produce a "tar.gz" file, which should be redacted
   * and shouldn't exceed a maximum of 10MB.
   * <li>The runner should create a symlink with the name "output.gz" in the
   * runner's process directory and reference it to the tar.gz produced by the
   * runner.
   * <li>Both the symlink and the referenced tar.gz should be accessible by
   * the user and group as specified in
   * {@link com.cloudera.csd.descriptors.ServiceDescriptor}#getRunAs().
   * </ol>
   */
  @NotNull
  @Valid
  RunnerDescriptor getRunner();
}
