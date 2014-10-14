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
package com.cloudera.csd.validation.components;

import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.descriptors.parameters.BoundedParameter;
import com.cloudera.csd.validation.SdlTestUtils;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
import com.cloudera.csd.validation.constraints.Expression;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceDescriptorValidatorImplTest {

  @Autowired
  private ServiceDescriptorValidatorImpl validator;

  @Test
  public void testFullSdl() {
    assertEquals(ImmutableSet.of(),
        validator.getViolations(SdlTestUtils.getParserSdl("service_full.sdl")));
  }

  @Test
  public void testKmsSdl() {
    assertEquals(ImmutableSet.of(),
        validator.getViolations(SdlTestUtils.getParserSdl("service_kms.sdl")));
  }

  @Test
  public void testNotNullCheck() {
    ConstraintViolation<ServiceDescriptor> violation = violation("service_missingName.sdl");
    assertEquals(violation.getPropertyPath().toString(), "name");
    assertConstraint(violation, EntityTypeFormat.class);
  }

  @Test
  public void testNestedBean() {
    ConstraintViolation<ServiceDescriptor> violation = violation("service_missingRunAsUser.sdl");
    assertEquals(violation.getPropertyPath().toString(), "runAs.user");
    assertConstraint(violation, NotBlank.class);
  }

  @Test
  public void testEmptyAndBlankString() {
    Set<String> errors = validate("service_emptyName.sdl");
    assertEquals(2, errors.size());
    assertTrue(errors.contains("service.name must only contain upper case letters, numbers, and underscores"));
    assertTrue(errors.contains("service.label must be present and not blank"));
  }

  @Test
  public void testEmptyCollection() {
    Set<String> errors = validate("service_emptyRequiredCollection.sdl");
    assertEquals("service.parameters[0].validValues must be present and not empty", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testValid() {
    Set<ConstraintViolation<ServiceDescriptor>> violations = violations("service_valid.sdl");
    assertTrue(violations.isEmpty());
  }

  @Test
  public void testServiceTypeLowerCase() {
    Set<String> errors = validate("service_serviceTypeNotUppercase.sdl");
    assertEquals("service.name must only contain upper case letters, numbers, and underscores", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testNonUniqueKeys() {
    Set<String> errors = validate("service_nonunique.sdl");
    assertEquals("service.roles[].name must be unique in list", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testMultipleNonUniqueKeys() {
    Set<String> errors = validate("service_nonuniqueMultiple.sdl");
    assertEquals(2, errors.size());
    assertTrue(errors.contains("service.parameters[].name must be unique in list"));
    assertTrue(errors.contains("service.parameters[].configName must be unique in list"));
  }

  @Test
  public void testMultipleErrors() {
    Set<ConstraintViolation<ServiceDescriptor>> violations = violations("service_multipleErrors.sdl");
    assertEquals(2, violations.size());
  }

  @Test
  public void testBadParcelUrl() {
    Set<String> errors = validate("service_badparcelurl.sdl");
    assertEquals("service.parcel.repoUrl must be a valid url", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testBadHdfsDirPerm() {
    Set<String> errors = validate("service_badHdfsDirPerm.sdl");
    assertEquals("service.hdfsDirs[].permissions must be a four digit octal file permission", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testCompatibilty() {
    Set<String> errors = validate("service_badCompatibility.sdl");
    assertEquals("service.compatibility.generation must be more than 1", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testBadServiceDependency() {
    Set<String> errors = validate("service_badDependencyType.sdl");
    assertEquals("service.serviceDependencies[0].name must be a valid service type", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testNonUniqueRoleGlobally() {
    Set<String> errors = validate("service_nonUniqueRoleGlobal.sdl");
    assertEquals("service.roles[0].name conflicts with a built-in role type", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testNonUniqueServiceGlobally() {
    Set<String> errors = validate("service_nonUniqueServiceGlobal.sdl");
    assertEquals("service.name conflicts with a built-in service type", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testGoodBoundsOnParameters() {
    Set<ConstraintViolation<ServiceDescriptor>> errors = violations("service_goodBoundsParameter.sdl");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testBadBoundsOnParameters() {
    Set<String> errors = validate("service_badBoundsParameter.sdl");
    assertEquals("service.parameters[0].my_param must satisfy \"" +
        BoundedParameter.class.getAnnotation(Expression.List.class).value()[0].value() + "\"",
        Iterables.getOnlyElement(errors));
  }

  @Test
  public void testGoodAutoConfigShares() {
    Set<ConstraintViolation<ServiceDescriptor>> errors = violations("service_goodAutoConfigShares.sdl");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testBadAutoConfigShares() {
    Set<String> errors = validate("service_badAutoConfigShares.sdl");
    assertEquals("service.roles[0].parameters must add up to 100 autoconfig shares",
        Iterables.getOnlyElement(errors));
  }

  @Test
  public void testTopologyRangeCheck() {
    Set<String> errors = validate("service_topologyRangeCheck.sdl");
    assertEquals("service.roles[0].topology must satisfy " +
            "\"minInstances == null or maxInstances == null or minInstances <= maxInstances\"",
        Iterables.getOnlyElement(errors));
  }

  @Test
  public void testInvalidSslServerReference() {
    Set<String> errors = validate("service_badSslServerRef.sdl");
    assertEquals(
        ImmutableSet.of(
            "service.roles.startRunner.environmentVariables has invalid " +
            "substitutions [ssl_enabled]. Substitutions available: [host, " +
            "ssl_client_truststore_password, group, " +
            "ssl_client_truststore_location, user]"),
        errors);
  }

  @Test
  public void testInvalidSslClientReference() {
    Set<String> errors = validate("service_badSslClientRef.sdl");
    assertEquals(
        ImmutableSet.of(
            "service.roles.startRunner.environmentVariables has invalid " +
            "substitutions [ssl_client_truststore_location]. Substitutions " +
            "available: [ssl_server_keystore_location, host, " +
            "ssl_server_keystore_keypassword, ssl_server_keystore_password, " +
            "ssl_enabled, group, user]"),
        errors);
  }

  private void assertConstraint(ConstraintViolation<?> violation, Class<?> constraint) {
    ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
    assertEquals(constraint, descriptor.getAnnotation().annotationType());
  }

  private ConstraintViolation<ServiceDescriptor> violation(String sdl) {
    Set<ConstraintViolation<ServiceDescriptor>> violations = violations(sdl);
    return Iterables.getOnlyElement(violations);
  }

  private Set<ConstraintViolation<ServiceDescriptor>> violations(String sdl) {
    return validator.getViolations(SdlTestUtils.getValidatorSdl(sdl));
  }

  private Set<String> validate(String sdl) {
    return validator.validate(SdlTestUtils.getValidatorSdl(sdl));
  }
}
