// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;
import com.cloudera.validation.constraints.FilePermission;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PermissionsDescriptorValidatorImplTest {

  @Autowired
  private PermissionsDescriptorValidatorImpl validator;

  @Test
  public void testValid() {
    Set<ConstraintViolation<PermissionsDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserPermissionsJson("good_permissions.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testMinimal() {
    Set<ConstraintViolation<PermissionsDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getParserPermissionsJson("empty.json"));
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testBad() {
    Set<String> violations = validate("bad_permissions.json");
    assertEquals(2, violations.size());
  }

  @Test
  public void testBadOctal() {
    Set<ConstraintViolation<PermissionsDescriptor>> violations =
        validator.getViolations(ParcelTestUtils.getValidatorPermissionsJson("bad_octal_permissions.json"));
    assertEquals(2, violations.size());
    for (ConstraintViolation<PermissionsDescriptor> violation : violations) {
      assertTrue(violation.getConstraintDescriptor().getAnnotation() instanceof FilePermission);
    }
  }

  private Set<String> validate(String path) {
    return validator.validate(ParcelTestUtils.getValidatorPermissionsJson(path));
  }
}
