// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.ValidServiceDependencyValidator;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidServiceDependencyValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  private ValidServiceDependencyValidator validator;

  @Test
  public void testServiceTypeIsNotValid() {
    Set<String> services = ImmutableSet.of("foo", "bar");
    validator = new ValidServiceDependencyValidatorImpl(services);
    assertFalse(validator.isValid("fuz", context));
  }

  @Test
  public void testServiceTypeIsValid() {
    Set<String> services = ImmutableSet.of("foo", "bar");
    validator = new ValidServiceDependencyValidatorImpl(services);
    assertTrue(validator.isValid("foo", context));
  }
}
