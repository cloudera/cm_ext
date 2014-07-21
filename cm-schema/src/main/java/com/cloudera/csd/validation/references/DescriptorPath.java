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

import java.lang.reflect.Method;

import javax.validation.ElementKind;
import javax.validation.Path;

/**
 * A Path with additional information. This object is
 * immutable and so each operation returns a new descriptor path.
 */
public interface DescriptorPath extends Path {

  /**
   * A Node with additional information.
   */
  public interface DescriptorNode extends Node {
  }

  /**
   * A Property node that also stores the method.
   */
  public interface PropertyDescriptorNode extends DescriptorNode, PropertyNode {

    /**
     * @return the method associated with this node.
     */
    Method getMethod();
  }

  /**
   * A Bean Node that stores if it is named and the bean.
   */
  public interface BeanDescriptorNode extends DescriptorNode, BeanNode {

    /**
     * @return true if this bean is named. A named bean is annotated with
     * the {@link com.cloudera.csd.validation.references.annotations.Named}
     * annotation.
     */
    boolean isNamed();

    /**
     * @return The bean associated with this node.
     */
    Object getBean();
  }

  /**
   * @return the head of the path. This is the current element.
   */
  DescriptorNode getHeadNode();

  /**
   * @return the tail of the path. This is the root element.
   */
  DescriptorNode getTailNode();

  /**
   * Filters out any element types that are not in the list provided.
   * @return a new path
   */
  DescriptorPath onlyInclude(ElementKind... whitelist);

  /**
   * Removes the head element.
   * @return a new path
   */
  DescriptorPath removeFromHead();

  /**
   * Returns true if a node exists in the path
   * with the given name and kind.
   */
  boolean contains(String name, ElementKind kind);
}
