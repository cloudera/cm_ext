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

import com.cloudera.csd.validation.references.DescriptorPath;
import com.cloudera.csd.validation.references.DescriptorPath.BeanDescriptorNode;
import com.cloudera.csd.validation.references.DescriptorPath.DescriptorNode;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.lang.annotation.ElementType;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * An implementation of the ConstraintViolation interface. This allows the
 * reference violation to look and feel like violation produced by the
 * hibernate validator.
 *
 * @param <T> The root object type.
 */
public class ReferenceConstraintViolation<T> implements ConstraintViolation<T>, Serializable {

  private final String interpolatedMessage;
  private final T rootBean;
  private final Object value;
  private final DescriptorPath propertyPath;
  private final Object leafBeanInstance;
  private final ConstraintDescriptor<?> constraintDescriptor;
  private final String messageTemplate;
  private final Class<T> rootBeanClass;
  private final ElementType elementType;
  private final Object[] executableParameters;
  private final Object executableReturnValue;

  /**
   * Build a ReferenceConstraintViolation from a reference violaiton.
   */
  public static <T> ReferenceConstraintViolation<T> forViolation(String message,
                                                              Object currentBean,
                                                              Object currentValue,
                                                              DescriptorPath path,
                                                              ElementType elementType) {

    DescriptorNode first = path.getTailNode();
    T rootBean = (T)first.as(BeanDescriptorNode.class).getBean();
    Class<T> rootBeanClass = (Class<T>)rootBean.getClass();

    return new ReferenceConstraintViolation<T>(
      message,
      message,
      rootBeanClass,
      rootBean,
      currentBean,
      currentValue,
      path,
      null,
      elementType,
      null,
      null
    );
  }

  public ReferenceConstraintViolation(String messageTemplate,
                                      String interpolatedMessage,
                                      Class<T> rootBeanClass,
                                      T rootBean,
                                      Object leafBeanInstance,
                                      Object value,
                                      DescriptorPath propertyPath,
                                      ConstraintDescriptor<?> constraintDescriptor,
                                      ElementType elementType,
                                      Object[] executableParameters,
                                      Object executableReturnValue) {
    this.messageTemplate = messageTemplate;
    this.interpolatedMessage = interpolatedMessage;
    this.rootBean = rootBean;
    this.value = value;
    this.propertyPath = propertyPath;
    this.leafBeanInstance = leafBeanInstance;
    this.constraintDescriptor = constraintDescriptor;
    this.rootBeanClass = rootBeanClass;
    this.elementType = elementType;
    this.executableParameters = executableParameters;
    this.executableReturnValue = executableReturnValue;
  }

  @Override
  public String getMessage() {
    return interpolatedMessage;
  }

  @Override
  public String getMessageTemplate() {
    return messageTemplate;
  }

  @Override
  public T getRootBean() {
    return rootBean;
  }

  @Override
  public Class<T> getRootBeanClass() {
    return rootBeanClass;
  }

  @Override
  public Object getLeafBean() {
    return leafBeanInstance;
  }

  @Override
  public Object getInvalidValue() {
    return value;
  }

  @Override
  public Path getPropertyPath() {
    return propertyPath.onlyInclude(ElementKind.PROPERTY);
  }

  @Override
  public ConstraintDescriptor<?> getConstraintDescriptor() {
    return this.constraintDescriptor;
  }

  @Override
  public <C> C unwrap(Class<C> type) {
    if (type.isAssignableFrom(ConstraintViolation.class)) {
      return type.cast(this);
    }
    throw new UnsupportedOperationException("Type " + type + " not supported for unwrapping.");
  }

  @Override
  public Object[] getExecutableParameters() {
    return executableParameters;
  }

  @Override
  public Object getExecutableReturnValue() {
    return executableReturnValue;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ReferenceConstraintViolation<?>)) {
      return false;
    }
    ReferenceConstraintViolation<?> that = (ReferenceConstraintViolation<?>) o;
    return Objects.equal(this.interpolatedMessage, that.interpolatedMessage) &&
           Objects.equal(this.rootBean, that.rootBean) &&
           Objects.equal(this.value, that.value) &&
           Objects.equal(this.propertyPath, that.propertyPath) &&
           Objects.equal(this.leafBeanInstance, that.leafBeanInstance) &&
           Objects.equal(this.constraintDescriptor, that.constraintDescriptor) &&
           Objects.equal(this.messageTemplate, that.messageTemplate) &&
           Objects.equal(this.rootBeanClass, that.rootBeanClass) &&
           Objects.equal(this.elementType, that.elementType) &&
           Objects.equal(this.executableParameters, that.executableParameters) &&
           Objects.equal(this.executableReturnValue, that.executableReturnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(interpolatedMessage,
                            rootBean,
                            value,
                            propertyPath,
                            leafBeanInstance,
                            constraintDescriptor,
                            messageTemplate,
                            rootBeanClass,
                            elementType,
                            executableParameters,
                            executableReturnValue);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
                  .omitNullValues()
                  .add("interpolatedMessage", interpolatedMessage)
                  .add("rootBean", rootBean)
                  .add("value", value)
                  .add("propertyPath", propertyPath)
                  .add("leafBeanInstance", leafBeanInstance)
                  .add("constraintDescriptor", constraintDescriptor)
                  .add("messageTemplate", messageTemplate)
                  .add("rootBeanClass", rootBeanClass)
                  .add("elementType", elementType)
                  .add("executableParameters", executableParameters)
                  .add("executableReturnValue", executableReturnValue)
                  .toString();
  }
}

