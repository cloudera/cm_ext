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
package com.cloudera.csd.validation.references.constraints;

import com.cloudera.csd.validation.references.ReferenceConstraint;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.util.ReflectionUtils;

/**
 * Provides common common functionality for
 * all reference constraints.
 *
 * @param <T> the root object type.
 */
public abstract class AbstractReferenceConstraint<T> implements ReferenceConstraint<T> {

  /**
   * Normalizes the results of invoking the method on the object.
   * If the method returns a single string, it is wrapped in a list.
   * If the result is a map, the values of the map are used to create
   * the collection of strings.
   * @param method the method to invoke
   * @param obj the object to invoke the method on.
   * @return the list of returned ids. Nulls are filtered out.
   */
  protected Collection<String> getIds(Method method, Object obj) {
    Object result = ReflectionUtils.invokeMethod(method, obj);
    Collection<String> ids;
    if (result instanceof Collection) {
      ids = (Collection<String>)result;
    } else if (result instanceof Map) {
      ids = ((Map<?, String>) result).values();
    } else {
      ids = ImmutableList.of((String) result);
    }
    return Collections2.filter(ids, new Predicate<String>() {
      public boolean apply(@Nullable String input) {
        return (input != null);
      }
    });
  }
}
