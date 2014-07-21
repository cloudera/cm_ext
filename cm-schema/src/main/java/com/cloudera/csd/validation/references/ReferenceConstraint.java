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

import com.cloudera.csd.validation.references.DescriptorPath.DescriptorNode;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * A reference constraint used by the ReferenceValidator.
 * @param <T> The type of the root object.
 */
public interface ReferenceConstraint<T> {

  /**
   * The node type this constraint refers to.
   */
  Class<? extends DescriptorNode> getNodeType();

  /**
   * The annotation this constraint applies to.
   */
  Class<? extends Annotation> getAnnotationType();

  /**
   * The method called when the validator finds a node of the
   * desired type that has the desired annotation. This should
   * return any constraint violations found.
   *
   * @param annotation the instance of the annotation.
   * @param obj the object
   * @param path the path of the object.
   * @param allowedRefs all the allowed references.
   * @return any constraint violations.
   */
  List<ConstraintViolation<T>> checkConstraint(Annotation annotation,
                                               Object obj,
                                               DescriptorPath path,
                                               SetMultimap<ReferenceType, String> allowedRefs);
}
