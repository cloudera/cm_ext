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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A constraint for fields where a pathname is expected, will ensure that
 * the pathname starts with some subdirectory name.
 * Eg. "file1" FAIL
 *     "dir1/" FAIL
 *     "/file4" FAIL
 *     "dir2/file2" OK
 */
@Constraint(validatedBy = RequiresSubdirValidator.class)
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface RequiresSubdir {

  String message() default "{custom.validation.constraints.RequiresSubdir.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
