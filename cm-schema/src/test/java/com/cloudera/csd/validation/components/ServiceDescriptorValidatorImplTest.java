// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.components;

import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.validation.SdlTestUtils;
import com.cloudera.csd.validation.constraints.EntityTypeFormat;
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
    assertEquals("service.parameters[].validValues must be present and not empty", Iterables.getOnlyElement(errors));
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
    Set <String> errors = validate("service_badDependencyType.sdl");
    assertEquals("service.serviceDependencies[].name must be a valid service type", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testNonUniqueRoleGlobally() {
    Set <String> errors = validate("service_nonUniqueRoleGlobal.sdl");
    assertEquals("service.roles[].name conflicts with a built-in role type", Iterables.getOnlyElement(errors));
  }

  @Test
  public void testNonUniqueServiceGlobally() {
    Set <String> errors = validate("service_nonUniqueServiceGlobal.sdl");
    assertEquals("service.name conflicts with a built-in service type", Iterables.getOnlyElement(errors));
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
