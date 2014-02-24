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
package com.cloudera.validation;

import com.google.common.collect.Iterators;

import java.util.NoSuchElementException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * A Constraint Validation Factory that uses a Spring bean factory
 * to construct validators.
 */
public class BeanConstraintValidatorFactory implements ConstraintValidatorFactory {

  private final ConfigurableListableBeanFactory beanFactory;

  public BeanConstraintValidatorFactory(ConfigurableListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
    try {
      String beanName = getBeanName(key);
      if (!beanFactory.isPrototype(beanName)) {
        String msg = "Bean [%s] must be of prototype scope.";
        throw new IllegalArgumentException(String.format(msg, beanName));
      }
      return beanFactory.getBean(beanName, key);
    } catch (NoSuchElementException e) {
      // The factory does not know about the bean it creates it.
      return beanFactory.createBean(key);
    }
  }

  @Override
  public void releaseInstance(ConstraintValidator <?, ?> instance) {
    String beanName = getBeanName(instance.getClass());
    beanFactory.destroyBean(beanName, instance);
  }

  private String getBeanName(Class<?> key) {
    String[] beanNames = beanFactory.getBeanNamesForType(key);
    return Iterators.getOnlyElement(Iterators.forArray(beanNames));
  }
}
