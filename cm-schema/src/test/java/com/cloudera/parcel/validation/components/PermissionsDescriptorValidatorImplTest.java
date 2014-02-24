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
