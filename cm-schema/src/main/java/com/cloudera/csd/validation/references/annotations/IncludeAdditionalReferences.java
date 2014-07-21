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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Additional references might be needed in
 * a descriptor. These references could come
 * from another referenced descriptor in a different
 * scope. For example, a service command
 * refers to a role command, so the role command
 * name needs to exist in that role type.
 *
 * This annotation declares what additional references
 * to add.
 */
@Inherited
@Target({ TYPE })
@Retention(RUNTIME)
public @interface IncludeAdditionalReferences {

  /**
   * @return the property name used to determine which
   * references should be added.
   */
  String value();
}
