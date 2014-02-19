// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
