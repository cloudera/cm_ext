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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.SdlTestUtils;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceMonitoringDefinitionsDescriptorValidatorImplTest {

  @Autowired
  private ServiceMonitoringDefinitionsDescriptorValidatorImpl validator;

  @Test
  public void testGoodServiceMonitoringDefinitionsDescriptor()
      throws Exception {
    assertEquals(0,
        validator.validate(SdlTestUtils.getValidatorMdl(
                "monitoring/service_with_good_metrics.mdl")).size());
  }

  @Test
  public void testMetricsWithBadPrefixesMDL() throws Exception {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      validations =
        validator.getViolations(SdlTestUtils.getValidatorMdl(
            "monitoring/service_with_bad_prefixes.mdl"));
    assertEquals(2, validations.size());
    ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>
      serviceViolation = null;
    ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>
      roleViolation = null;
    for (ConstraintViolation<ServiceMonitoringDefinitionsDescriptor> validation :
         validations) {
      if (validation.getMessage().contains("service_metric_1")) {
        serviceViolation = validation;
      }
      if (validation.getMessage().contains("role_metric_2")) {
        roleViolation = validation;
      }
    }
    assertNotNull(serviceViolation);
    assertNotNull(roleViolation);
    assertEquals("ECHO.service_metric_1.name",
        serviceViolation.getPropertyPath().toString());
    assertEquals("ECHO.ECHOROLE.role_metric_2.name",
        roleViolation.getPropertyPath().toString());
  }

  public void testEmptyMetrics() {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> errors =
        violations("monitoring/service_emptyMetrics.mdl");
    assertTrue(errors.isEmpty());
  }

  public void testNullMetrics() {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> errors =
        violations("monitoring/service_nullMetrics.mdl");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testValidMetrics() {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> errors =
        violations("monitoring/service_metrics.mdl");
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testMetricMissingName() {
    Set<String> errors = validate("monitoring/service_missingMetricName.mdl");
    assertEquals(1, errors.size());
  }

  @Test
  public void testMetricBadName() {
    Set<String> errors = validate("monitoring/service_badMetricName.mdl");
    // See fixture for the metrics we expect to be badly named.
    assertEquals(4, errors.size());
  }

  @Test
  public void testBadWeightingMetric() {
    Set<String> errors = validate("monitoring/service_badweightingmetric.mdl");
    // We have one good weighting metric name and two bad (one referring to
    // itself one just referring to a bogus metric).
    assertEquals(2, errors.size());
  }

  @Test
  public void testOverridingNameForAggregateForAService() {
    assertEquals(0,
        validator.validate(SdlTestUtils.getValidatorMdl(
                "monitoring/echo_with_only_name_for_aggregate.mdl")).size());
  }

  @Test
  public void testOverridingNameForAggregateForARole() {
    assertEquals(0,
        validator.validate(SdlTestUtils.getValidatorMdl(
                "monitoring/echo_with_only_role_name_for_aggregate.mdl")).size());
  }

  @Test
  public void testFullMdl() {
    assertEquals(0,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/service_full.mdl")).size());
  }

  @Test
  public void testUnreachableParent() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/service_unreachable_parent.mdl")).size());
  }

  @Test
  public void testInconsistentMetricLabel() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_metric_label.mdl")).size());
  }

  @Test
  public void testInconsistentMetricDescription() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_metric_description.mdl")).size());
  }

  @Test
  public void testInconsistentMetricNumerator() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_metric_numerator.mdl")).size());
  }

  @Test
  public void testInconsistentMetricDenominator() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_metric_denominator.mdl")).size());
  }

  @Test
  public void testInconsistentWeightingMetric() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_weighting_metric.mdl")).size());
  }

  @Test
  public void testInconsistentCounter() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/inconsistent_metric_counter.mdl")).size());
  }

  @Test
  public void testInvalidEntityName() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/service_bad_entity_name.mdl")).size());
  }

  @Test
  public void testUnknownParent() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/service_unknown_parent.mdl")).size());
  }

  @Test
  public void testUnknownAttributes() {
    assertEquals(2,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/service_unknown_attributes.mdl")).size());
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      violations(String mdl) {
    return validator.getViolations(SdlTestUtils.getValidatorMdl(mdl));
  }

  private Set<String> validate(String mdl) {
    return validator.validate(SdlTestUtils.getValidatorMdl(mdl));
  }

  @Test
  public void testAttributeNotPrefixedWithServiceName() {
    Set<String> errors = validate("monitoring/service_badAttributeName.mdl");
    // See fixture for the metrics we expect to be badly named.
    assertEquals(2, errors.size());
  }

  @Test
  public void testUnknownAdditionalImmutableAttribute() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/unknown_additional_immutable_attribute.mdl")).size());
  }

  @Test
  public void testUnknownAdditionalMutableAttribute() {
    assertEquals(1,
        validator.validate(SdlTestUtils.getValidatorMdl(
            "monitoring/unknown_additional_mutable_attribute.mdl")).size());
  }
}