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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DenominatorValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private DenominatorValidator validator;

  @Before
  public void setUpCounterMetricNameValidatorTest() {
    validator = new DenominatorValidator(serviceDescriptor);
  }

  @Test
  public void testNotEndingWithRateWithDenominator() {
    setName("bytes_read");
    setDenominator("seconds");
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testNotEndingWithRateWithoutDenominator() {
    setName("bytes_read");
    setDenominator(null);
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testCounterNotEndingWithRateWithDenominator() {
    setName("bytes_read");
    setIsCounter(true);
    setDenominator("seconds");
    assertFalse(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testCounterNotEndingWithRateWithoutDenominator() {
    setName("bytes_read");
    setIsCounter(true);
    setDenominator(null);
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testEndingWithRateWithDenominator() {
    setName("bytes_read_rate");
    setDenominator("seconds");
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testEndingWithRateWithoutDenominator() {
    setName("bytes_read_rate");
    setDenominator(null);
    assertFalse(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testCounterEndingWithRateWithDenominator() {
    setName("bytes_read_rate");
    setIsCounter(true);
    setDenominator("seconds");
    assertFalse(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testCounterEndingWithRateWithoutDenominator() {
    setName("bytes_read_rate");
    setIsCounter(true);
    setDenominator(null);
    assertFalse(validator.validate(metric, root).isEmpty());
  }

}
