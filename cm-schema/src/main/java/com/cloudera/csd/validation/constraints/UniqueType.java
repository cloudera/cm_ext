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
package com.cloudera.csd.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * A constraint that ensures that only one object of specific runtime class type
 * is present in the given {@link java.util.Collection}.
 * <p>
 * This constraint should be used iff the underlying
 * {@link java.lang.annotation.ElementType} uses a {@link java.util.Collection}.
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = {UniqueTypeValidator.class})
public @interface UniqueType {

  /**
   * If we should skip null values in the check.
   * Defaults to true.
   */
  boolean skipNulls() default true;

  String message() default "{custom.validation.constraints.UniqueType.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
