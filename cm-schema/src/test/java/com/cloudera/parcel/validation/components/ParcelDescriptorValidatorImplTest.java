// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;
import com.google.common.collect.Iterables;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ParcelDescriptorValidatorImplTest {

  @Autowired
  private ParcelDescriptorValidatorImpl validator;

  @Test
  public void testValid() {
    Set<ConstraintViolation<ParcelDescriptor>> violations = validator.getViolations(ParcelTestUtils.FULL_DESCRIPTOR);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testMinimal() {
    Set<ConstraintViolation<ParcelDescriptor>> violations = validator.getViolations(ParcelTestUtils.getParserJson("minimal_parcel.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testEmpty() {
    Set<ConstraintViolation<ParcelDescriptor>> violations = validator.getViolations(ParcelTestUtils.getParserJson("empty.json"));
    assertEquals(9, violations.size());
  }

  @Test
  public void testMissingName() {
    Set<String> violations = validate("missing_name.json");
    assertEquals(1, violations.size());
    assertEquals("parcel.name must be present and not blank", Iterables.getOnlyElement(violations));
  }

  @Test
  public void testMissingSchema() {
    Set<String> violations = validate("missing_schema.json");
    assertEquals(1, violations.size());
    assertEquals("parcel.schema_version must be present", Iterables.getOnlyElement(violations));
  }

  @Test
  public void testWrongSchema() {
    Set<String> violations = validate("wrong_schema.json");
    assertEquals(1, violations.size());
    assertEquals("parcel.schema_version must be between 1 and 1", Iterables.getOnlyElement(violations));
  }

  private void assertConstraint(ConstraintViolation<?> violation, Class<?> constraint) {
    ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
    assertEquals(constraint, descriptor.getAnnotation().annotationType());
  }

  private ConstraintViolation<ParcelDescriptor> violation(String path) {
    Set<ConstraintViolation<ParcelDescriptor>> violations = violations(path);
    return Iterables.getOnlyElement(violations);
  }

  private Set<ConstraintViolation<ParcelDescriptor>> violations(String path) {
    return validator.getViolations(ParcelTestUtils.getParserJson(path));
  }

  private Set<String> validate(String path) {
    return validator.validate(ParcelTestUtils.getValidatorJson(path));
  }
}
