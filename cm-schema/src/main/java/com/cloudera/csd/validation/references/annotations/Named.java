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
 * This annotation is used to name a property
 * in the descriptor. This is useful when printing
 * out error messages. In addition, for reference
 * validation, this name is used by
 * {@link com.cloudera.csd.validation.references.annotations.Referenced}
 */
@Inherited
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Named {

  /**
   * By default, the property is: name.
   */
  String DEFAULT_PROPERTY_NAME = "name";

  /**
   * The property name used to determine the name of
   * the descriptor.
   */
  String value() default DEFAULT_PROPERTY_NAME;
}
