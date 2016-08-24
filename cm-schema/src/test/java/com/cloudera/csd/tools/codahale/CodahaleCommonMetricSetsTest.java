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
import com.cloudera.csd.tools.codahale.CodahaleCommonMetricSets.CommonMetric;
import com.cloudera.csd.tools.codahale.CodahaleCommonMetricSets.CommonMetricAdapter;
import com.cloudera.csd.tools.codahale.CodahaleCommonMetricSets.MetricServlet2XAdapter;
import com.cloudera.csd.tools.codahale.CodahaleCommonMetricSets.MetricSet;
import com.cloudera.csd.tools.codahale.CodahaleCommonMetricSets.Version;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.CodahaleMetricType;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CodahaleCommonMetricSetsTest {

  private static CommonMetricAdapter testAdapater = new CommonMetricAdapter() {
    @Override
    public String getName(CommonMetric commonMetric) {
      return "adapted_name_" + commonMetric.getCodahaleMetric().getName();
    }

    @Override
    public String getContext(CommonMetric commonMetric) {
      return "adapted_context::" + commonMetric.getCodahaleMetric().getName();
    }
  };

  @Test
  public void testCodahaleMetricsGeneration() {
    for (MetricSet metricSet : MetricSet.values()) {
      testCodahaleMetricsGeneration(metricSet);
    }
  }

  private void testCodahaleMetricsGeneration(MetricSet metricSet) {
    List<CodahaleMetric> generatedMetrics =
        CodahaleCommonMetricSets.generateCodahaleMetricsForMetricSet(
        metricSet,
        Version.CODAHALE_3_X_METRIC_SETS,
        testAdapater);
    int expectedSize = 0;
    for (CommonMetric metric : metricSet.getMetrics()) {
      if (metric.isApplicableToVersion(
          Version.CODAHALE_3_X_METRIC_SETS)) {
        expectedSize++;
      }
    }
    assertEquals(expectedSize, generatedMetrics.size());
    for (CodahaleMetric metric : generatedMetrics) {
      assertEquals(metric.getName(), CodahaleMetricType.GAUGE, metric.getType());
      assertTrue(metric.getName(), metric.getName().startsWith("adapted_name_"));
      String commonMetricName =
          metric.getName().substring("adapted_name_".length()).toUpperCase();
      CommonMetric commonMetric = CommonMetric.valueOf(commonMetricName);
      assertTrue(metric.getName(), metricSet.getMetrics().contains(
          commonMetric));
      verifyCodahaleMetric(commonMetric, metric);
      assertTrue(metric.getName(),
                 metric.getContext().startsWith("adapted_context::"));
    }
  }

  @Test
  public void testMetricsServlet2XAdapter() {
    for (MetricSet metricSet : MetricSet.values()) {
      testMetricsServlet2XAdapter(metricSet);
    }
  }

  private void testMetricsServlet2XAdapter(MetricSet metricSet) {
    assertNotNull(metricSet);
    MetricServlet2XAdapter adapter = new MetricServlet2XAdapter("::", metricSet);
    List<MetricDescriptor> generatedMetrics =
        CodahaleCommonMetricSets.generateMetricDescritptorsForMetricSet(
            metricSet,
            Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
            adapter,
            "my_service");
    int expectedSize = 0;
    for (CommonMetric metric : metricSet.getMetrics()) {
      if (metric.isApplicableToVersion(
          Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS)) {
        expectedSize++;
      }
    }
    assertEquals(expectedSize, generatedMetrics.size());
    for (MetricDescriptor metric : generatedMetrics) {
      assertFalse(metric.getName(), metric.isCounter());
      String parts[] = metric.getContext().split("::");
      String mungedMetricName = parts[parts.length - 1];
      String reversedMetricName = "";
      for (char cc : mungedMetricName.toCharArray()) {
        if (Character.isUpperCase(cc)) {
          reversedMetricName += "_";
        }
        reversedMetricName += cc;
      }
      CommonMetric commonMetric = CommonMetric.valueOf(
          reversedMetricName.toUpperCase());
      assertEquals(commonMetric.getCodahaleMetric().getName(),
                   reversedMetricName.toLowerCase());
      assertTrue(metric.getName(), metricSet.getMetrics().contains(
          commonMetric));
      String metricNameForContext =
          CodahaleCommonMetricSets.getMetricsServletName(commonMetric);
          metric.getName().substring("my_service".length() + 1);
      verifyMetricDescriptor(commonMetric, metric);

      switch (metricSet) {
        case MEMORY:
          assertEquals(String.format("jvm::memory::%s",
                                     metricNameForContext),
                       metric.getContext());
          break;
        case THREAD_STATE:
          assertEquals(String.format("jvm::%s",
                                     commonMetric.getCodahaleMetric().getName()),
                       metric.getContext());
          break;
        default:
          fail();
      }
    }
  }

  @Test
  public void testMetricDescriptorsGeneration() {
    for (MetricSet metricSet : MetricSet.values()) {
      testMetricDescriptorsGeneration(metricSet);
    }
  }

  private void testMetricDescriptorsGeneration(MetricSet metricSet) {
    List<MetricDescriptor> generatedMetrics =
        CodahaleCommonMetricSets.generateMetricDescritptorsForMetricSet(
            metricSet,
            Version.CODAHALE_3_X_METRIC_SETS,
            testAdapater,
            "SERVICE");
    int expectedSize = 0;
    for (CommonMetric metric : metricSet.getMetrics()) {
      if (metric.isApplicableToVersion(
          Version.CODAHALE_3_X_METRIC_SETS)) {
        expectedSize++;
      }
    }
    assertEquals(expectedSize, generatedMetrics.size());
    for (MetricDescriptor metric : generatedMetrics) {
      assertTrue(metric.getName(),
                 metric.getName().startsWith("service_adapted_name_"));
      String commonMetricName =
          metric.getName().substring("service_adapted_name_".length())
              .toUpperCase();
      CommonMetric commonMetric = CommonMetric.valueOf(commonMetricName);
      assertTrue(metric.getName(), metricSet.getMetrics().contains(
          commonMetric));
      verifyMetricDescriptor(commonMetric, metric);
      assertTrue(metric.getName(),
                 metric.getContext().startsWith("adapted_context::"));
    }
  }

  private void verifyMetricDescriptor(CommonMetric commonMetric,
                                      MetricDescriptor metric) {
    CodahaleMetric expected = commonMetric.getCodahaleMetric();
    assertEquals(expected.getLabel(), metric.getLabel());
    assertEquals(expected.getDescription(), metric.getDescription());
    assertEquals(expected.getNumeratorUnit(), metric.getNumeratorUnit());
  }

  private void verifyCodahaleMetric(CommonMetric commonMetric,
                                    CodahaleMetric metric) {
    CodahaleMetric expected = commonMetric.getCodahaleMetric();
    assertEquals(expected.getLabel(), metric.getLabel());
    assertEquals(expected.getDescription(), metric.getDescription());
    assertEquals(expected.getNumeratorUnit(), metric.getNumeratorUnit());
  }
}
