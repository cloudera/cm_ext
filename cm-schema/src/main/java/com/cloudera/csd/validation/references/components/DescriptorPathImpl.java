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
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.Referenced;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.validation.ElementKind;

/**
 * An implementation of a DescriptorPath
 */
public class DescriptorPathImpl implements DescriptorPath {

  private final LinkedList<DescriptorNodeImpl> list = Lists.newLinkedList();
  private static final Joiner JOINER = Joiner.on(".");

  public static class DescriptorNodeImpl implements DescriptorPath.DescriptorNode {

    private final String name;
    private final boolean iterable;
    private final Integer index;
    private final Object key;
    private final ElementKind kind;

    public DescriptorNodeImpl(String name,
                              boolean iterable,
                              Integer index,
                              Object key,
                              ElementKind kind) {
      this.name = name;
      this.iterable = iterable;
      this.index = index;
      this.key = key;
      this.kind = kind;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public boolean isInIterable() {
      return this.iterable;
    }

    @Override
    public Integer getIndex() {
      return this.index;
    }

    @Override
    public Object getKey() {
      return this.key;
    }

    @Override
    public ElementKind getKind() {
      return this.kind;
    }

    @Override
    public <T extends Node> T as(Class<T> nodeType) {
      return nodeType.cast(this);
    }

    @Override
    public String toString() {
      return this.name;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof DescriptorNodeImpl) {
        DescriptorNodeImpl o = (DescriptorNodeImpl)other;
        return Objects.equal(this.name, o.name);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.name.hashCode();
    }
  }

  public static class PropertyNode extends DescriptorNodeImpl implements PropertyDescriptorNode {
    private final Method method;
    public PropertyNode(String name,
                        boolean iterable,
                        Integer index,
                        Method method) {
      super(name, iterable, index, null, ElementKind.PROPERTY);
      this.method = method;
    }

    @Override
    public Method getMethod() {
      return this.method;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof PropertyNode) {
        if (super.equals(other)) {
          PropertyNode oProperty = (PropertyNode)other;
          return Objects.equal(this.method, oProperty.method);
        }
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(super.hashCode(), this.method.hashCode());
    }
  }

  public static class BeanNode extends DescriptorNodeImpl implements BeanDescriptorNode {
    private final boolean isNamed;
    private final Object bean;

    public BeanNode(String name,
                    Object key,
                    Object bean,
                    boolean isNamed) {
      super(name, false, null, key, ElementKind.BEAN);
      this.isNamed = isNamed;
      this.bean = bean;
    }

    @Override
    public Object getBean() {
      return this.bean;
    }


    @Override
    public boolean isNamed() {
      return this.isNamed;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof BeanNode) {
        if (super.equals(other)) {
          BeanNode o = (BeanNode)other;
          return Objects.equal(this.bean, o.bean);
        }
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(super.hashCode(), this.bean.hashCode());
    }
  }

  @Override
  public DescriptorNode getHeadNode() {
    if (this.list.size() == 0) {
      return null;
    }
    return this.list.getLast();
  }

  @Override
  public DescriptorNode getTailNode() {
    if (this.list.size() == 0) {
      return null;
    }
    return this.list.getFirst();
  }

  @Override
  public DescriptorPathImpl removeFromHead() {
    DescriptorPathImpl newPath = makeCopy();
    newPath.list.removeLast();
    return newPath;
  }

  @Override
  public Iterator<Node> iterator() {
    return ImmutableList.<Node>copyOf(list).iterator();
  }

  /**
   * Given a method, creates and adds a property node to the path.
   *
   * @param method the method
   * @param isIterable true if it is an iterable
   * @return a new path
   */
  public DescriptorPathImpl addPropertyNode(Method method, boolean isIterable) {
    Preconditions.checkNotNull(method);
    String name = ReflectionHelper.propertyNameOfGetter(method);
    return addToHead(new PropertyNode(name, isIterable, null, method));
  }

  /**
   * Given a bean object, creates and adds a bean node to the path.
   *
   * @param obj the bean.
   * @return a new path.
   */
  public DescriptorPathImpl addBeanNode(Object obj) {
    Preconditions.checkNotNull(obj);
    String[] names = new String[]{obj.getClass().getSimpleName()};
    Named named = ReflectionHelper.findAnnotation(obj.getClass(), Named.class);
    if (named != null) {
      names = new String[]{ReflectionHelper.propertyValueByName(obj, named.value(), String.class)};
    }
    // If there is a @Referenced annotation and it has a hardcoded "as" value,
    // use that name instead.
    Referenced referenced = ReflectionHelper.findAnnotation(obj.getClass(), Referenced.class);
    if ((referenced != null) && (referenced.as().length != 0)) {
      names = referenced.as();
    }
    return addToHead(new BeanNode(
        names.length == 1 ? names[0] : names.toString(),
        null, obj, (named != null)));
  }

  @Override
  public DescriptorPathImpl onlyInclude(ElementKind... whitelist) {
    Set<ElementKind> kinds = ImmutableSet.copyOf(whitelist);
    DescriptorPathImpl newPath = new DescriptorPathImpl();
    for (DescriptorNodeImpl node : this.list) {
      if (kinds.contains(node.getKind())) {
        newPath.list.add(node);
      }
    }
    return newPath;
  }

  @Override
  public boolean contains(String name, ElementKind kind) {
     for (DescriptorNodeImpl node : this.list) {
      if (kind.equals(node.getKind()) && node.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return JOINER.join(list);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DescriptorPathImpl) {
      DescriptorPathImpl other = (DescriptorPathImpl)o;
      return Objects.equal(this.list, other.list);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.list.hashCode();
  }

  private DescriptorPathImpl addToHead(DescriptorNodeImpl node) {
    DescriptorPathImpl newPath = makeCopy();
    newPath.list.add(node);
    return newPath;
  }

  private DescriptorPathImpl makeCopy() {
    DescriptorPathImpl newPath = new DescriptorPathImpl();
    newPath.list.addAll(this.list);
    return newPath;
  }

}
