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

import static org.junit.Assert.*;

import com.cloudera.csd.validation.references.annotations.Named;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

public class ReflectionHelperTest {

  private Method parentAgetInt = method(ParentA.class, "getInt");
  private Method parentAgetInt2 = method(ParentA.class, "getInt2");

  private Class<?> childA = ChildA.class;
  private Object childAObj = new ChildA();
  private Method childAFoo = method(ChildA.class, "foo");
  private Method childABar = method(ChildA.class, "bar");
  private Method childAgetVar = method(ChildA.class, "getVar");
  private Method childAisVar2 = method(ChildA.class, "isVar2");

  private Class<?> childB = ChildB.class;
  private Method childBMeth = ReflectionUtils.findMethod(ChildB.class, "meth");

  public interface InterfaceA {

    @Max(4)
    @NotNull
    void foo();

    int getInt();
  }

  @Named("blah")
  public abstract static class ParentA implements InterfaceA {
    @Override
    public void foo() { };

    @Override
    public int getInt() {
      return 1;
    }

    public int getInt2() {
      return 2;
    }
  }

  @Component
  public static class ChildA extends ParentA {

    @Min(3)
    @NotNull
    public String bar() {
      return "hello";
    }

    public String getVar() {
      return "var";
    }

    public boolean isVar2() {
      return false;
    }

    public void setVar3(String var) {
      // do nothing.
    }
  }

  public static class ChildB extends ParentA {
    public String meth() {
      return "method";
    }
  }

  @Test
  public void testHasAnnotation() {
    assertTrue(ReflectionHelper.hasAnnotation(childAFoo, NotNull.class));
    assertTrue(ReflectionHelper.hasAnnotation(childABar, NotNull.class));
    assertFalse(ReflectionHelper.hasAnnotation(childAFoo, NotBlank.class));
  }

  @Test
  public void testFindAnnotationMethod() {
    assertEquals(3l,
            ReflectionHelper.findAnnotation(childABar, Min.class).value());
    assertNull(ReflectionHelper.findAnnotation(childABar, Max.class));
    assertEquals(4l,
            ReflectionHelper.findAnnotation(childAFoo, Max.class).value());
  }

  @Test
  public void testFindAnnotationClass() {
    assertNotNull(ReflectionHelper.findAnnotation(childA, Component.class));
    assertEquals("blah",
            ReflectionHelper.findAnnotation(childA, Named.class).value());
    assertNull(ReflectionHelper.findAnnotation(childA, Scope.class));
  }

  @Test
  public void testInvokeMethod() {
    assertEquals("hello",
            ReflectionHelper.invokeMethod(childABar, childAObj));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvokeMethodBadMethod() {
    ReflectionHelper.invokeMethod(childBMeth, childAObj);
  }

  @Test
  public void testGetterMethods() {
    assertEquals(Sets.newHashSet(parentAgetInt,
                                 parentAgetInt2,
                                 childAgetVar,
                                 childAisVar2),
                 ReflectionHelper.getterMethods(childA));
    assertEquals(2, ReflectionHelper.getterMethods(childB).size());
  }

  @Test
  public void testPropetyNameOfGetter() {
    assertEquals("var", ReflectionHelper.propertyNameOfGetter(childAgetVar));
    assertEquals("var2", ReflectionHelper.propertyNameOfGetter(childAisVar2));
  }

  @Test(expected = IllegalStateException.class)
  public void testPropertyNameOfGetterBadName() {
    ReflectionHelper.propertyNameOfGetter(childABar);
  }

  @Test
  public void testPropertyValueByName() {
    assertEquals("var", ReflectionHelper.propertyValueByName(childAObj, "var", String.class));
    assertEquals(false, ReflectionHelper.propertyValueByName(childAObj, "var2", boolean.class));
  }

  private static Method method(Class<?> clazz, String name) {
    Method m = ReflectionUtils.findMethod(clazz, name);
    assertNotNull("Method name: " + name + " for class: " + clazz.getSimpleName(), m);
    return m;
  }
}

