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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.cloudera.csd.validation.monitoring.MonitoringConventions;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

public class CounterMetricNameValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private CounterMetricNameValidator validator;
  private MonitoringValidationContext context;

  @Before
  public void setUpCounterMetricNameValidatorTest() {
    validator = new CounterMetricNameValidator();
    context = new MonitoringValidationContext(serviceDescriptor);
  }

  @Test
  public void testGoodCounter() {
    setName("bytes_read");
    assertTrue(validator.validate(context, metric, root).isEmpty());
  }

  @Test
  public void testCounterWithRateSuffix() {
    setName("bytes_read" + MonitoringConventions.RATE_SUFFIX);
    setIsCounter(true);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, metric, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "ends with _rate"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }

  @Test
  public void testNonCounterWithRateSuffix() {
    setName("bytes_read");
    setIsCounter(false);
    assertTrue(validator.validate(context, metric, root).isEmpty());
  }

  @Test
  public void testCounterReadMetricInvalid() {
    // This is a contrived metric name. It's not valid format (it includes two
    // underscores) but it's simple enough to verify that we are validating the
    // read-path metric name.
    setName("total__count");
    setIsCounter(true);
    List<ConstraintViolation<Object>> validations =
        validator.validate(context, metric, root);
    assertFalse(validations.isEmpty());
    ConstraintViolation<Object> validation = Iterables.getOnlyElement(
        validations);
    assertTrue(validation.toString(),
               validation.getMessage().contains(
                   "visible name"));
    String path = validation.getPropertyPath().toString();
    assertEquals(String.format("%s.name", SERVICE_NAME), path);
  }

  @Test
  public void testCounterMetricVisibleNameCollidesWithExistingMetric() {
    setName("bytes_read");
    setIsCounter(true);
    setServiceMetrics(ImmutableList.of(newMetricWithName("bytes_read_rate")));
    context = new MonitoringValidationContext(serviceDescriptor);
    assertFalse(validator.validate(context, metric, root).isEmpty());
  }

}
