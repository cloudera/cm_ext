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

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.tools.impala.ImpalaMetricTypes.StatisticalMetricType;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImpalaMetricAdapterTest {

  private static final boolean COUNTER = true;

  private ImpalaMetricAdapter adapter;

  @Before
  public void setupMetricAdapterTest() {
    adapter = new ImpalaMetricAdapter();
  }

  @Test
  public void testEmptyValidSource() throws Exception {

    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/impala/empty_but_valid.json").getPath();
    adapter.init(source, null);
    assertEquals("test_service", adapter.getServiceName());
    assertEquals(0, adapter.getServiceMetrics().size());
    assertEquals(0, adapter.getRoleNames().size());
    assertEquals(0, adapter.getEntityNames().size());
  }

  @Test
  public void testGaugeMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/impala/valid.json").getPath();
    adapter.init(source, null);
    assertEquals(2, adapter.getRoleNames().size());
    List<MetricDescriptor> metrics =
        getMetricsForRole(adapter, "test_role1_metrics");
    assertEquals(2, metrics.size());
    validateSimpleMetric(metrics.get(1),
                         adapter.getServiceName(),
                         "test_role1_metric2",
                         !COUNTER);
  }

  @Test
  public void testCounterMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/impala/valid.json").getPath();
    adapter.init(source, null);
    assertEquals(2, adapter.getRoleNames().size());
    List<MetricDescriptor> metrics =
        getMetricsForRole(adapter, "test_role1_metrics");
    assertEquals(2, metrics.size());
    validateSimpleMetric(metrics.get(0),
                         adapter.getServiceName(),
                         "test_role1_metric1",
                         COUNTER);
  }

  @Test
  public void testStatisticalMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/impala/valid.json").getPath();
    adapter.init(source, null);
    List<MetricDescriptor> metrics =
        getMetricsForEntity(adapter, "test_entity1_metrics");
    assertEquals(StatisticalMetricType.values().length,
                 metrics.size());
    for (MetricDescriptor metric : metrics) {
      if (!metric.getName().contains("metric2")) {
        // metric2 is the histogram, metric1 is the timer.
        continue;
      }
      StatisticalMetricType type =
          getStatisticalTypeForMetric(metric.getName());
      validateComplexMetric(
          metric,
          adapter.getServiceName(),
          type.makeMetricName("test_entity1_metric2"),
          type.makeMetricLabel("test_entity1_metric2_label"),
          type.makeMetricDescription("test_entity1_metric2_description"),
          "threads",
          type.isCounter());
    }
  }

  @Test
  public void testNameForCounterMetricOverride() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/impala/name_for_counter_metrics_overrides.json")
        .getPath();
    adapter.init(source, null);
    List<MetricDescriptor> metrics = adapter.getServiceMetrics();
    assertEquals(StatisticalMetricType.values().length, metrics.size());
    for (MetricDescriptor metric : metrics) {
      if (metric.isCounter()) {
        assertTrue(metric.getName(),
                   metric.getName().equals(
                       "test_service_counter_name_override"));
      }
    }
    // This role has a gauge and a counter and the name override should be
    // ignored.
    for (MetricDescriptor metric : adapter.getRoleMetrics("test_role1_metrics")) {
      assertTrue(metric.getName(),
                 metric.getName().equals("test_service_test_role1_metric1") ||
                     metric.getName().equals("test_service_test_role1_metric2"));
    }

    // This entity has a histogram and a timer defined with overrides. Make sure
    // they were used.
    for (MetricDescriptor metric :
         adapter.getEntityMetrics("test_entity1_metrics")) {
      if (metric.isCounter()) {
        assertTrue(
            metric.getName(),
            metric.getName().equals(
                "test_service_entity1_metric1_counter_override") ||
            metric.getName().equals(
                "test_service_entity1_metric2_counter_override"));
      }
    }
  }

  private void validateComplexMetric(MetricDescriptor metric,
                                     String serviceName,
                                     String baseMetricName,
                                     String expectedLabel,
                                     String expectedDescription,
                                     String expectedCounterUnit,
                                     boolean isCounter) {

    String expectedName =
        String.format("%s_%s", serviceName, baseMetricName);
    assertEquals(expectedName, metric.getName());
    assertEquals(expectedLabel, metric.getLabel());
    assertEquals(expectedDescription, metric.getDescription());
    assertEquals(isCounter, metric.isCounter());
    if (isCounter) {
      assertEquals(expectedCounterUnit, metric.getNumeratorUnit());
      assertNull(metric.getDenominatorUnit(), metric.getDenominatorUnit());
    } else {
      assertEquals("bytes", metric.getNumeratorUnit());
      assertEquals("second", metric.getDenominatorUnit());
    }
    assertNull(metric.getWeightingMetricName());
  }

  private StatisticalMetricType getStatisticalTypeForMetric(String name) {
    for (StatisticalMetricType type : StatisticalMetricType.values()) {
      if (name.endsWith(type.suffix())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Metric " + name + " is not a " +
                                       "statistical metric");
  }

  private List<MetricDescriptor> getMetricsForRole(ImpalaMetricAdapter adapter,
                                                   String roleName) {
    for (String role : adapter.getRoleNames()) {
      if (roleName.equals(role)) {
        return adapter.getRoleMetrics(role);
      }
    }
    throw new IllegalArgumentException("Could not find metrics for " + roleName);
  }

  private List<MetricDescriptor> getMetricsForEntity(ImpalaMetricAdapter adapter,
                                                     String entityName) {
    for (String entity : adapter.getEntityNames()) {
      if (entityName.equals(entity)) {
        return adapter.getEntityMetrics(entity);
      }
    }
    throw new IllegalArgumentException("Could not find metrics for " +
                                       entityName);
  }

  private void validateSimpleMetric(MetricDescriptor metric,
                                    String serviceName,
                                    String expectedBaseName,
                                    boolean isCounter) {
    String expectedName =
        String.format("%s_%s", serviceName, expectedBaseName);
    assertEquals(expectedName, metric.getName());
    assertEquals(expectedBaseName + "_label", metric.getLabel());
    assertEquals(expectedBaseName + "_description", metric.getDescription());
    assertEquals(isCounter, metric.isCounter());
    assertEquals("bytes", metric.getNumeratorUnit());
    if (isCounter) {
      assertNull(metric.getDenominatorUnit());
    } else {
      assertEquals("second", metric.getDenominatorUnit());
    }
    assertNull(metric.getWeightingMetricName());
  }
}
