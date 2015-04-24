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
package com.cloudera.csd.tools.codahale;

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.HistogramMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.MeterMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.TimerMetricType;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CodahaleMetricAdapterTest {

  private static final boolean COUNTER = true;

  private CodahaleMetricAdapter adapter;

  @Before
  public void setupMetricAdapterTest() {
    adapter = new CodahaleMetricAdapter();
  }

  @Test
  public void testEmptyValidSource() throws Exception {

    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/codahale/empty_but_valid.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
    assertEquals("test_service", adapter.getServiceName());
    assertEquals(0, adapter.getServiceMetrics().size());
    assertEquals(0, adapter.getRoleNames().size());
    assertEquals(0, adapter.getEntityNames().size());
  }

  @Test
  public void testGaugeMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/codahale/valid.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
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
        "/com/cloudera/csd/tools/codahale/valid.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
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
  public void testHistogramMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/codahale/valid.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
    List<MetricDescriptor> metrics =
        getMetricsForEntity(adapter, "test_entity1_metrics");
    assertEquals(TimerMetricType.values().length +
                 HistogramMetricType.values().length,
                 metrics.size());
    for (MetricDescriptor metric : metrics) {
      if (!metric.getName().contains("metric2")) {
        // metric2 is the histogram, metric1 is the timer.
        continue;
      }
      HistogramMetricType type = getHistogramTypeForMetric(metric.getName());
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
  public void testTimerMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/codahale/timers.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
    List<MetricDescriptor> metrics = adapter.getServiceMetrics();
    assertEquals(2 * TimerMetricType.values().length, metrics.size());
    for (MetricDescriptor metric : metrics) {
      String metricName = metric.getName().contains("metric2") ? "metric2" :
          "metric1";
      TimerMetricType type = getTimerTypeForMetric(metric.getName());
      validateTimerMetric(
          metric,
          adapter.getServiceName(),
          type.makeMetricName("test_svc_" + metricName),
          type.makeMetricLabel(String.format("test_svc_%s_label", metricName)),
          type.makeMetricDescription(
              String.format("test_svc_%s_description", metricName)),
          "calls",
          "foobar",
          type);
    }
  }

  @Test
  public void testMeterMetrics() throws Exception {
    String source = this.getClass().getResource(
        "/com/cloudera/csd/tools/codahale/meters.json").getPath();
    adapter.init(source, CodahaleMetricAdapter.DEFAULT_CONVENTIONS);
    List<MetricDescriptor> metrics = adapter.getServiceMetrics();
    assertEquals(2 * MeterMetricType.values().length, metrics.size());
    for (MetricDescriptor metric : metrics) {
      String metricName = metric.getName().contains("metric2") ? "metric2" :
          "metric1";
      MeterMetricType type = getMeterTypeForMetric(metric.getName());
      validateComplexMetric(
          metric,
          adapter.getServiceName(),
          type.makeMetricName("test_svc_" + metricName),
          type.makeMetricLabel(String.format("test_svc_%s_label", metricName)),
          type.makeMetricDescription(
              String.format("test_svc_%s_description", metricName)),
          "bytes",
          type.isCounter());
    }
  }

  private void validateTimerMetric(MetricDescriptor metric,
                                   String serviceName,
                                   String baseMetricName,
                                   String expectedLabel,
                                   String expectedDescription,
                                   String expectedCounterUnit,
                                   String expectedRateOverride,
                                   TimerMetricType type) {

    String expectedName =
        String.format("%s_%s", serviceName, baseMetricName);
    assertEquals(expectedName, metric.getName());
    assertEquals(expectedLabel, metric.getLabel());
    assertEquals(expectedDescription, metric.getDescription());
    assertEquals(type.isCounter(), metric.isCounter());
    if (type.isCounter()) {
      assertEquals(expectedCounterUnit, metric.getNumeratorUnit());
      assertNull(metric.getDenominatorUnit(), metric.getDenominatorUnit());
    } else if (type.isRate()) {
      assertEquals(expectedCounterUnit, metric.getNumeratorUnit());
      assertEquals(expectedRateOverride, metric.getDenominatorUnit());
    } else {
      assertEquals("bytes", metric.getNumeratorUnit());
      assertEquals("second", metric.getDenominatorUnit());
    }
    assertNull(metric.getWeightingMetricName());
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

  private HistogramMetricType getHistogramTypeForMetric(String name) {
    for (HistogramMetricType type : HistogramMetricType.values()) {
      if (name.endsWith(type.suffix())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Metric " + name + " is not a " +
                                       "histogram metric");
  }

  private TimerMetricType getTimerTypeForMetric(String name) {
    for (TimerMetricType type : TimerMetricType.values()) {
      if (name.endsWith(type.suffix())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Metric " + name + " is not a " +
                                       "timer metric");
  }

  private MeterMetricType getMeterTypeForMetric(String name) {
    for (MeterMetricType type : MeterMetricType.values()) {
      if (name.endsWith(type.suffix())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Metric " + name + " is not a " +
                                       "meter metric");
  }

  private List<MetricDescriptor> getMetricsForRole(CodahaleMetricAdapter adapter,
                                                   String roleName) {
    for (String role : adapter.getRoleNames()) {
      if (roleName.equals(role)) {
        return adapter.getRoleMetrics(role);
      }
    }
    throw new IllegalArgumentException("Could not find metrics for " + roleName);
  }

  private List<MetricDescriptor> getMetricsForEntity(CodahaleMetricAdapter adapter,
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
