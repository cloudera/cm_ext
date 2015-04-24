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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * A set of reflection helper functions. Where available,
 * this class uses other reflection libraries that are available.
 */
public class ReflectionHelper {

  private static final Set<String> GETTER_PREFIXES = ImmutableSet.of("get", "is");

  /**
   * Returns true if the method or any of its super methods
   * have the annotation.
   *
   * @param method the method.
   * @param annotationType the annotation class.
   * @param <A> the annotation type
   * @return true if the annotation is on this method.
   */
  public static <A extends Annotation> boolean hasAnnotation(Method method, Class<A> annotationType) {
    return (findAnnotation(method, annotationType) != null);
  }

  /**
   * Finds the annotation of the provided method by searching
   * the entire method hierarchy.
   *
   * @param method the method.
   * @param annotationType the annotation class.
   * @param <A> the annotation type.
   * @return the annotation if it is found. Null otherwise.
   */
  @Nullable
  public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
    return AnnotationUtils.findAnnotation(method, annotationType);
  }

  /**
   * Finds the annotation of the provided class by searching
   * the entire class hierarchy.
   *
   * @param clazz the class.
   * @param annotationType the annotation class.
   * @param <A> the annotation type.
   * @return the annotation if it exists. Null otherwise.
   */
  @Nullable
  public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
    return AnnotationUtils.findAnnotation(clazz, annotationType);
  }

  /**
   * Invoke the method against the target object with no
   * arguments. If there is an exception invoking the method,
   * an IllegalStateException is thrown.
   *
   * @param method the method.
   * @param target the object to execute the method against.
   * @return the result of the method call.
   */
  @Nullable
  public static Object invokeMethod(Method method, Object target) {
    return ReflectionUtils.invokeMethod(method, target);
  }

  /**
   * Return the set of getter methods for the class.
   *
   * @param clazz the class.
   * @return the set of getter methods.
   */
  public static Set<Method> getterMethods(Class<?> clazz) {
    Preconditions.checkNotNull(clazz);
    try {
      ImmutableSet.Builder<Method> builder = ImmutableSet.builder();
      BeanInfo info = Introspector.getBeanInfo(clazz);
      for (PropertyDescriptor p : info.getPropertyDescriptors()) {
        Method getter = p.getReadMethod();
        if (getter != null) {
          // Don't want to include any of the methods inherited from object.
          if (!getter.getDeclaringClass().equals(Object.class)) {
            builder.add(getter);
          }
        }
      }
      return builder.build();
    } catch (IntrospectionException e) {
      throw new IllegalStateException("Could not introspect on " + clazz, e);
    }
  }

  /**
   * Returns the name of the property associated with the getter method.
   * If the method is not a a getter, an IllegalStateException is thrown.
   *
   * @param method the method.
   * @return the property name.
   */
  public static String propertyNameOfGetter(Method method) {
    Preconditions.checkNotNull(method);
    String name = method.getName();
    for (String prefix : GETTER_PREFIXES) {
      if (name.startsWith(prefix)) {
        return Introspector.decapitalize(name.substring(prefix.length()));
      }
    }
    throw new IllegalStateException("Method is malformed " + method.getName());
  }

  /**
   * The value of the property is returned for the target object.
   *
   * @param targetObject the target object.
   * @param propertyName the property name.
   * @param clazz the class of the value.
   * @param <T> the type of the value.
   * @return the value of the property
   */
  @Nullable
  public static <T> T propertyValueByName(Object targetObject, String propertyName, Class<T> clazz) {
    try {
      PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(targetObject, propertyName);
      Object result = invokeMethod(desc.getReadMethod(), targetObject);
      return (T)result;
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Could not invoke " + propertyName, e);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException("Could not invoke " + propertyName, e);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Could not invoke " + propertyName, e);
    }
  }

  /**
   * Returns the property getter method if one exists, 'null' otherwise.
   * @param obj The object which has the property 'propertyName'
   * @param propertyName The property name, e.g., "name".
   * @return
   */
  @Nullable
  public static Method propertyGetter(Object obj, String propertyName) {
    Preconditions.checkNotNull(obj);
    Preconditions.checkNotNull(propertyName);
    try {
      PropertyDescriptor desc =
          PropertyUtils.getPropertyDescriptor(obj, propertyName);
      return desc.getReadMethod();
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(
          "Could not access " + propertyName + " of " +
          obj.getClass().getSimpleName(),
          e);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(
          "Could not access " + propertyName + " of " +
          obj.getClass().getSimpleName(),
          e);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(
          "No such property " + propertyName + " for " +
          obj.getClass().getSimpleName(),
          e);
    }
  }
}
