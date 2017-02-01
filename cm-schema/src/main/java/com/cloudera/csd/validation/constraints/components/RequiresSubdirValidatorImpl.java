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
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.RequiresSubdir;
import com.cloudera.csd.validation.constraints.RequiresSubdirValidator;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidatorContext;

public class RequiresSubdirValidatorImpl implements RequiresSubdirValidator {

  @Override
  public void initialize(RequiresSubdir constraint) {
  }

  private static boolean hasSubdirPrefix(String pathname) {
    return pathname.contains("/") && !pathname.startsWith("/") && !pathname.endsWith("/");
  }

  @Override
  public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }
    return hasSubdirPrefix(value);
  }
}
