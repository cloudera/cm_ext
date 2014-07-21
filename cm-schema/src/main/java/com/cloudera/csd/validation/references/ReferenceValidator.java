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
package com.cloudera.csd.validation.references;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * A validator that checks references in descriptors. This
 * is needed when a descriptor has fields that are references
 * to other entities in the descriptor.
 */
public interface ReferenceValidator {

  /**
   * Validates the descriptor and returns any
   * reference constraint violation.
   *
   * @param descriptor the descriptor.
   * @param <T> the type of the descriptor.
   * @return a set of constraint violations.
   */
  <T> Set<ConstraintViolation<T>> validate(T descriptor);
}
