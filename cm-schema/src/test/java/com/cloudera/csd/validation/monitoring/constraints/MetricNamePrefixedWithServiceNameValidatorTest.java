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
package com.cloudera.csd.validation.monitoring.constraints;

import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetricNamePrefixedWithServiceNameValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private MetricNamePrefixedWithServiceNameValidator validator;

  @Before
  public void setupMetricNamePrefixedWithServiceNameValidatorTest() {
    // We should do all comparisons in a case insensitive way.
    validator =
        new MetricNamePrefixedWithServiceNameValidator(serviceDescriptor);
  }

  @Test
  public void testValidMetric() {
    setName(SERVICE_NAME.toLowerCase() + "_a");
    Assert.assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testEmptyName() {
    setName("");
    List<ConstraintViolation<Object>> validations = validator.validate(metric,
                                                                       root);
    Assert.assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    Assert.assertTrue(validation.toString(),
                      validation.getMessage().contains(
                       "does not start with the service name"));
    String path = validation.getPropertyPath().toString();
    Assert.assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }

  @Test
  public void testInvaidMetricNotPrefixed() {
    setName("foo");
    List<ConstraintViolation<Object>> validations = validator.validate(metric,
                                                                       root);
    Assert.assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    Assert.assertTrue(validation.toString(),
                      validation.getMessage().contains(
                          "Metric 'foo' does not start with the service name"));
    String path = validation.getPropertyPath().toString();
    Assert.assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }
}
