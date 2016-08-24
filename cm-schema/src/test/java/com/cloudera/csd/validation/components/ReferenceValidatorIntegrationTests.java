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

import static org.junit.Assert.*;

import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.validation.SdlTestUtils;
import com.cloudera.csd.validation.references.ReferenceValidator;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceValidatorIntegrationTests {

  private final static Joiner JOINER = Joiner.on("\n");

  @Autowired
  ReferenceValidator refValidator;

  @Test
  public void testServiceInit() {
    runTest("service_serviceInit.sdl", 1);
  }

  @Test
  public void testExternalLinks() {
    runTest("service_externalLinks.sdl", 1);
  }

  @Test
  public void testConfigWriter() {
    runTest("service_configWriter.sdl", 3);
  }

  @Test
  public void testGracefulStop() {
    runTest("service_gracefulStop.sdl", 2);
  }

  @Test
  public void testServiceCmd() {
    runTest("service_serviceCmd.sdl", 2);
  }

  @Test
  public void testSubsCreateHdfsDir() {
    runTest("service_subsCreateHdfsDir.sdl", 2);
  }

  @Test
  public void testSubsGracefulStop() {
    runTest("service_subsGracefulStop.sdl", 2);
  }

  @Test
  public void testSubsProcessRunner() {
    runTest("service_subsProcessRunner.sdl", 2);
  }

  @Test
  public void testSubsLoggingDir() {
    runTest("service_subsLoggingDir.sdl", 1);
  }

  @Test
  public void testSubsExternalLinks() {
    runTest("service_subsExternalLinks.sdl", 1);
  }

  @Test
  public void testScopeGracefulStop() {
    runTest("service_scopeGracefulStop.sdl", 1);
  }

  @Test
  public void testScopeConfigWriter() {
    runTest("service_scopeConfigWriter.sdl", 1);
  }

  @Test
  public void testScopeServiceCmd() {
    runTest("service_scopeServiceCmd.sdl", 2);
  }

  @Test
  public void testPeerGeneratorAndPlacementRule() {
    runTest("service_peerGeneratorAndPlacementRule.sdl", 0);
  }

  private void runTest(String filename, int errorsExpected) {
    ServiceDescriptor descriptor = SdlTestUtils.getReferenceValidatorSdl(filename);

    Collection<ConstraintViolation<ServiceDescriptor>> violations = refValidator.validate(descriptor);
    ImmutableSet.Builder<String> violationStrings = ImmutableSet.builder();
    for (ConstraintViolation<ServiceDescriptor> violation : violations) {
      String message = violation.getMessage();
      String relativePath = violation.getPropertyPath().toString();
      String error = String.format("%s.%s %s", "service", relativePath, message);
      violationStrings.add(error);
    }
    System.out.println(JOINER.join(violationStrings.build()));
    assertEquals("Found Errors: \n" + JOINER.join(violationStrings.build()),
            errorsExpected,
            violations.size());
  }
}
