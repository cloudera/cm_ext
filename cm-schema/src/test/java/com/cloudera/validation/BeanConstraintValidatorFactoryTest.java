// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.validation;

import com.cloudera.validation.BeanConstraintValidatorFactory;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BeanConstraintValidatorFactoryTest {

  @Mock
  private ConfigurableListableBeanFactory beanFactory;

  @InjectMocks
  private BeanConstraintValidatorFactory factory;

  @Mock
  private TestConstraintValidator validator;
  private Class<TestConstraintValidator> validatorType = TestConstraintValidator.class;
  private String validatorBeanName = "myvalidator";

  @Test
  public void testGetInstance() {
    String[] names = {validatorBeanName};
    when(beanFactory.getBeanNamesForType(validatorType)).thenReturn(names);
    when(beanFactory.isPrototype(validatorBeanName)).thenReturn(true);
    when(beanFactory.getBean(validatorBeanName, validatorType)).thenReturn(validator);
    TestConstraintValidator aValidator = factory.getInstance(validatorType);
    assertEquals(aValidator, validator);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetInstanceNotPrototype() {
    String[] names = {validatorBeanName};
    when(beanFactory.getBeanNamesForType(validatorType)).thenReturn(names);
    when(beanFactory.isPrototype(validatorBeanName)).thenReturn(false);
    factory.getInstance(validatorType);
  }

  @Test
  public void testGetInstanceNotInFactory() {
    String[] names = {};
    when(beanFactory.getBeanNamesForType(validatorType)).thenReturn(names);
    when(beanFactory.createBean(validatorType)).thenReturn(validator);
    TestConstraintValidator aValidator = factory.getInstance(validatorType);
    assertEquals(aValidator, validator);
  }

  @Test
  public void testReleaseInstance() {
    String[] names = {validatorBeanName};
    when(beanFactory.getBeanNamesForType(validator.getClass())).thenReturn(names);
    factory.releaseInstance(validator);
    verify(beanFactory).destroyBean(validatorBeanName, validator);
  }

  public static class TestConstraintValidator implements ConstraintValidator<Annotation, Object> {

    @Override
    public void initialize(Annotation constraintAnnotation) {}

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      return true;
    }
  }
}
