// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.ManifestDescriptor;
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
public class ManifestDescriptorValidatorImplTest {

  @Autowired
  private ManifestDescriptorValidatorImpl validator;

  @Test
  public void testValid() {
    Set<ConstraintViolation<ManifestDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserManifestJson("good_manifest.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testMinimal() {
    Set<ConstraintViolation<ManifestDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserManifestJson("empty.json"));
    assertEquals(2, violations.size());
  }

  @Test
  public void testBad() {
    Set<String> violations = validate("bad_manifest.json");
    assertEquals(3, violations.size());
  }

  private Set<String> validate(String path) {
    return validator.validate(ParcelTestUtils.getValidatorManifestJson(path));
  }
}
