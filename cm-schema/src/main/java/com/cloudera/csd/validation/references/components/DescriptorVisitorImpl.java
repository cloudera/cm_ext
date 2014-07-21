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
package com.cloudera.csd.validation.references.components;

import com.cloudera.csd.validation.references.DescriptorPath;
import com.cloudera.csd.validation.references.DescriptorVisitor;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * An Implementation of the DescriptorVisitor.
 */
public class DescriptorVisitorImpl implements DescriptorVisitor {

  /**
   * An abstract Node Processor that allows a child to only implement the
   * methods it needs.
   */
  public abstract static class AbstractNodeProcessor<T> implements NodeProcessor<T> {
    @Override
    public void beforeNode(Object obj,
                           DescriptorPath path) {}

    @Override
    public void afterNode(Object obj,
                          DescriptorPath oldPath) {}

    @Override
    @Nullable
    public T getResult() {
      return null;
    }
  }

  @Override
  public <T> T visit(Object rootObject, NodeProcessor<T> processor) {
    DescriptorPathImpl root = new DescriptorPathImpl();
    visit(rootObject, root, processor);
    return processor.getResult();
  }

  private <T> void visit(Object obj, DescriptorPathImpl path, NodeProcessor<T> processor) {
    Preconditions.checkNotNull(obj);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(processor);

    Class<?> clazz = obj.getClass();

    path = path.addBeanNode(obj);
    processor.beforeNode(obj, path);

    for (Method method : ReflectionHelper.getterMethods(clazz)) {
      Object t = ReflectionHelper.invokeMethod(method, obj);
      if (t != null) {
        boolean isIterable = (t instanceof Collection<?>);
        path = path.addPropertyNode(method, isIterable);
        processor.beforeNode(obj, path);

        if (ReflectionHelper.hasAnnotation(method, Valid.class)) {
          if (isIterable) {
            Collection<?> collection = (Collection<?>)t;
            for (Object c : collection) {
              visit(c, path, processor);
            }
          } else {
            visit(t, path, processor);
          }
        }
        DescriptorPathImpl oldPath = path;
        path = path.removeFromHead();
        processor.afterNode(obj, oldPath);
      }
    }
    DescriptorPathImpl oldPath = path;
    path.removeFromHead();
    processor.afterNode(obj, oldPath);
  }
}
