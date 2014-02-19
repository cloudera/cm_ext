// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AlternativesDescriptorValidatorImplTest {

  @Autowired
  private AlternativesDescriptorValidatorImpl validator;

  @Test
  public void testValid() {
    Set<ConstraintViolation<AlternativesDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserAlternativesJson("good_alternatives.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testMinimal() {
    Set<ConstraintViolation<AlternativesDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserAlternativesJson("empty.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testBad() {
    Set<String> violations = validate("bad_alternatives.json");
    assertEquals(4, violations.size());
  }

  private Set<String> validate(String path) {
    return validator.validate(ParcelTestUtils.getValidatorAlternativesJson(path));
  }
}
