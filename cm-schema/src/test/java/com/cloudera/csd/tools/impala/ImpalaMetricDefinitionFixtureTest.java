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
package com.cloudera.csd.tools.impala;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.cloudera.csd.tools.JsonUtil;
import com.cloudera.csd.tools.JsonUtil.JsonRuntimeException;
import com.cloudera.csd.tools.impala.ImpalaMetricTypes.ImpalaMetricType;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ImpalaMetricDefinitionFixtureTest {

  @Test
  public void testEmptyFixture() {
    InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(
          "/com/cloudera/csd/tools/impala/empty_but_valid.json");
      ImpalaMetricDefinitionFixture fixture = JsonUtil.valueFromStream(
          ImpalaMetricDefinitionFixture.class,
          in);
      assertNotNull(fixture.getServiceName());
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Test
  public void testValidFixture() {
    InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(
          "/com/cloudera/csd/tools/impala/valid.json");
      ImpalaMetricDefinitionFixture fixture = JsonUtil.valueFromStream(
          ImpalaMetricDefinitionFixture.class,
          in);
      assertEquals("test_service", fixture.getServiceName());
      assertEquals(1, fixture.getServiceMetrics().size());
      assertEquals(2, fixture.getRolesMetrics().size());
      assertEquals(2,
                   fixture.getRolesMetrics().get("test_role1_metrics").size());
      ImpalaMetric metric =
          fixture.getRolesMetrics().get("test_role1_metrics").get(1);
      validateMetric(metric, "test_role1_metric2", ImpalaMetricType.GAUGE);
      assertEquals(1,
                   fixture.getRolesMetrics().get("test_role2_metrics").size());
      metric =
          fixture.getRolesMetrics().get("test_role2_metrics").get(0);
      validateMetric(metric, "test_role2_metric1", ImpalaMetricType.STATISTICAL);
      assertEquals(1, fixture.getAdditionalServiceEntityTypesMetrics().size());

      metric =
          fixture.getAdditionalServiceEntityTypesMetrics().get(
              "test_entity1_metrics").get(0);
      assertEquals("threads", metric.getNumeratorForCounterMetric());
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Test(expected=JsonRuntimeException.class)
  public void testNullServiceName() {
    InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(
          "/com/cloudera/csd/tools/impala/null_service_name.json");
      JsonUtil.valueFromStream(ImpalaMetricDefinitionFixture.class, in);
      fail("We should not get here.");
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Test(expected=JsonRuntimeException.class)
  public void testEmptyServiceName() {
    InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(
          "/com/cloudera/csd/tools/impala/empty_service_name.json");
      JsonUtil.valueFromStream(ImpalaMetricDefinitionFixture.class, in);
      fail("We should not get here.");
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Test(expected=JsonRuntimeException.class)
  public void testInvalidMetricType() {
    InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(
          "/com/cloudera/csd/tools/impala/invalid_metric_type.json");
      JsonUtil.valueFromStream(ImpalaMetricDefinitionFixture.class, in);
      fail("We should not get here.");
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  private void validateMetric(ImpalaMetric metric,
                              String expectedName,
                              ImpalaMetricType expectedType) {
    assertEquals(expectedName, metric.getName());
    assertEquals(expectedType, metric.getType());
    assertEquals(expectedName + "_label", metric.getLabel());
    assertEquals(expectedName + "_description", metric.getDescription());
    assertEquals("bytes", metric.getNumeratorUnit());
    if (ImpalaMetricType.COUNTER.equals(metric.getType())) {
      assertNull(metric.getDenominatorUnit());
    } else {
      assertEquals("second", metric.getDenominatorUnit());
    }
  }
}
