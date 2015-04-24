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

import javax.annotation.Nullable;

/**
 * Used to traverse all the methods of the object.
 * Fields annotated with {@link javax.validation.Valid} are
 * recursively visited.
 */
public interface DescriptorVisitor {

  /**
   * The processor that the visitor uses to
   * callback when a method or object is found.
   * @param <T>
   */
  public interface NodeProcessor<T> {
    /**
     * Called before the node (method or bean) is looked at.
     *
     * @param obj the current object
     * @param path the current path of the node
     */
    void beforeNode(Object obj,
                    DescriptorPath path);

    /**
     * Called after the node (method or bean) is looked at.
     *
     * @param obj the current object
     * @param oldPath the path of the node just processed.
     */
    void afterNode(Object obj,
                   DescriptorPath oldPath);

    /**
     * When the visit complete, this result is returned.
     */
    @Nullable
    T getResult();
  }

  /**
   * Starts at the rootObject and visits each method
   * in the object. For each node (method and/or bean)
   * the appropriate method in the NodeProcessor is called.
   *
   * If the processor has a result object, it is returned from
   * visit.
   *
   * @param rootObject the root object
   * @param processor the processor.
   * @param <T> the result type
   * @return the result from the processor.
   */
  <T> T visit(Object rootObject, NodeProcessor<T> processor);
}
