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
import com.cloudera.csd.tools.MetricDescriptorImpl;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.CodahaleMetricType;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A class used to generate common codahale metrics, e.g., jvm memory metrics,
 * thread-state metrics, etc.
 */
public class CodahaleCommonMetricSets {

  /**
   * An enum describing the different codahale versions this
   * CodahaleCommonMetricSets supports.
   */
  public enum Version {
    // Jvm metrics exposed from the VirtualMachineMetrics class in codahale
    // versions 2.x.
    CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
    // Jvm metrics exposed from the different metric sets (e.g.,
    // MemoryUsageGaugeSet).
    CODAHALE_3_X_METRIC_SETS,
  }

  /**
   * A enum describing all the common metrics this class exposes.
   */
  public enum CommonMetric {
    TOTAL_INIT(
        new CodahaleMetric.Builder()
              .setName("total_init")
              .setLabel("JVM heap and non-heap initial memory")
              .setDescription("JVM heap and non-heap initial memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    TOTAL_USED(
        new CodahaleMetric.Builder()
              .setName("total_used")
              .setLabel("JVM heap and non-heap used memory")
              .setDescription("JVM heap and non-heap used memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    TOTAL_MAX(
        new CodahaleMetric.Builder()
              .setName("total_max")
              .setLabel("JVM heap and non-heap max used memory")
              .setDescription("JVM heap and non-heap max initial memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    TOTAL_COMMITTED(
        new CodahaleMetric.Builder()
              .setName("total_committed")
              .setLabel("JVM heap and non-heap committed memory")
              .setDescription("JVM heap and non-heap committed memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    HEAP_INIT(
        new CodahaleMetric.Builder()
              .setName("heap_init")
              .setLabel("JVM heap initial memory")
              .setDescription("JVM heap initial memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    HEAP_USED(
        new CodahaleMetric.Builder()
              .setName("heap_used")
              .setLabel("JVM heap used memory")
              .setDescription("JVM heap used memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    HEAP_MAX(
        new CodahaleMetric.Builder()
              .setName("heap_max")
              .setLabel("JVM heap max used memory")
              .setDescription("JVM heap max used memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    HEAP_COMMITTED(
        new CodahaleMetric.Builder()
              .setName("heap_committed")
              .setLabel("JVM heap committed memory")
              .setDescription("JVM heap committed memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                                   Version.CODAHALE_3_X_METRIC_SETS)),
    NON_HEAP_INIT(
        new CodahaleMetric.Builder()
              .setName("non_heap_init")
              .setLabel("JVM non heap initial memory")
              .setDescription("JVM non heap initial memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_3_X_METRIC_SETS)),
    NON_HEAP_USED(
        new CodahaleMetric.Builder()
              .setName("non_heap_used")
              .setLabel("JVM non heap used memory")
              .setDescription("JVM non heap used memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_3_X_METRIC_SETS)),
    NON_HEAP_MAX(
        new CodahaleMetric.Builder()
              .setName("non_heap_max")
              .setLabel("JVM non heap max used memory")
              .setDescription("JVM non heap max used memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_3_X_METRIC_SETS)),
    NON_HEAP_COMMITTED(
        new CodahaleMetric.Builder()
              .setName("non_heap_committed")
              .setLabel("JVM non heap committed memory")
              .setDescription("JVM non heap committed memory")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("bytes")
              .build(),
        ImmutableSet.of(Version.CODAHALE_3_X_METRIC_SETS)),
    THREAD_COUNT(
        new CodahaleMetric.Builder()
              .setName("thread_count")
              .setLabel("JVM daemon and non-daemon thread count")
              .setDescription("JVM daemon and non-daemon thread count")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("threads")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    DAEMON_THREAD_COUNT(
        new CodahaleMetric.Builder()
              .setName("daemon_thread_count")
              .setLabel("JVM daemon thread count")
              .setDescription("JVM daemon thread count")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("threads")
              .build(),
        ImmutableSet.of(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS,
                        Version.CODAHALE_3_X_METRIC_SETS)),
    DEADLOCKED_THREAD_COUNT(
        new CodahaleMetric.Builder()
              .setName("deadlocked_thread_count")
              .setLabel("JVM deadlocked thread count")
              .setDescription("JVM deadlocked thread count")
              .setCodahaleMetricType(CodahaleMetricType.GAUGE)
              .setNumerator("threads")
              .build(),
        ImmutableSet.of(Version.CODAHALE_3_X_METRIC_SETS));

    private final ImmutableSet<Version> applicableVersions;
    private final CodahaleMetric codahaleMetric;

    CommonMetric(
        CodahaleMetric codahaleMetric,
        ImmutableSet<Version> applicableVersions) {
      Preconditions.checkNotNull(codahaleMetric);
      Preconditions.checkNotNull(applicableVersions);
      Preconditions.checkArgument(!applicableVersions.isEmpty());
      this.applicableVersions = applicableVersions;
      this.codahaleMetric = codahaleMetric;
    }

    public boolean isApplicableToVersion(Version version) {
      Preconditions.checkNotNull(version);
      return applicableVersions.contains(version);
    }

    public CodahaleMetric getCodahaleMetric() {
      return codahaleMetric;
    }

    /**
     * Returns the metric name as it is defined in the codahale 3.x metric
     * set.
     * @return
     */
    public String getCodahale3xMetricName() {
      Preconditions.checkState(isApplicableToVersion(
          Version.CODAHALE_3_X_METRIC_SETS));
      String metricName = codahaleMetric.getName();
      metricName = metricName.replace("non_heap", "non-heap");
      metricName = metricName.replace("_", ".");
      return metricName;
    }
  }

  /**
   * The different metric sets supported. These are closely tied to the different
   * metric sets exposed by codahale 3.x.
   */
  public enum MetricSet {
    // JVM memory metrics, e.g., heap usage.
    MEMORY(ImmutableSet.of(CommonMetric.TOTAL_INIT,
                           CommonMetric.TOTAL_USED,
                           CommonMetric.TOTAL_MAX,
                           CommonMetric.TOTAL_COMMITTED,
                           CommonMetric.HEAP_INIT,
                           CommonMetric.HEAP_USED,
                           CommonMetric.HEAP_MAX,
                           CommonMetric.HEAP_COMMITTED,
                           CommonMetric.NON_HEAP_INIT,
                           CommonMetric.NON_HEAP_USED,
                           CommonMetric.NON_HEAP_MAX,
                           CommonMetric.NON_HEAP_COMMITTED)),
    // JVM thread-state metrics, e.g., thread count.
    THREAD_STATE(ImmutableSet.of(CommonMetric.THREAD_COUNT,
                                 CommonMetric.DAEMON_THREAD_COUNT,
                                 CommonMetric.DEADLOCKED_THREAD_COUNT));

    private ImmutableSet<CommonMetric> metrics;

    MetricSet(ImmutableSet<CommonMetric> metrics) {
      Preconditions.checkNotNull(metrics);
      Preconditions.checkArgument(!metrics.isEmpty());
      this.metrics = metrics;
    }

    public ImmutableSet<CommonMetric> getMetrics() {
      return metrics;
    }
  }

  /**
   * An interface that defines methods to transform the default metric names
   * to the actual names of the metrics as well as their context in their actual
   * serialized form.
   *
   * For example see MetricServlet220Adapter.
   */
  public interface CommonMetricAdapter {
    /**
     * Return the base name for the common metric.
     * @param commonMetric
     * @return
     */
    String getName(CommonMetric commonMetric);

    /**
     * Return the context to use to collect the common metric.
     * @param commonMetric
     * @return
     */
    String getContext(CommonMetric commonMetric);
  }

  /**
   * This is an adapter for codahale metric servlevt 2.x. This metric servlet
   * serializes all metrics from the VirtualMachingMetrics into well known and
   * non-configurable json nodes.
   */
  public static class MetricServlet2XAdapter
      implements CommonMetricAdapter {

    private final String separator;
    private final MetricSet metricSet;

    public MetricServlet2XAdapter(String separator,
                                  MetricSet metricSet) {
      Preconditions.checkNotNull(separator);
      Preconditions.checkNotNull(metricSet);
      this.separator = separator;
      this.metricSet = metricSet;
    }

    @Override
    public String getName(CommonMetric commonMetric) {
      switch (metricSet) {
        case MEMORY:
          return String.format("memory_%s",
                               commonMetric.getCodahaleMetric().getName());
        case THREAD_STATE:
          return commonMetric.getCodahaleMetric().getName();
        default:
          throw new UnsupportedOperationException("Unsupported metric set " +
                                                  metricSet.name());
      }
    }

    @Override
    public String getContext(CommonMetric commonMetric) {
      switch(metricSet) {
        case MEMORY:
          return StringUtils.join(
              ImmutableList.of("jvm",
                               "memory",
                               getMetricsServletName(commonMetric)),
              separator);
        case THREAD_STATE:
          return StringUtils.join(
              ImmutableList.of("jvm",
                               commonMetric.getCodahaleMetric().getName()),
              separator);
        default:
          throw new UnsupportedOperationException("Unsupported metric set " +
                                                  metricSet.name());
      }
    }
  }

  /**
   * Returns the metric name as it appears in codahale 2.x metric servlet.
   * @param commonMetric
   * @return
   */
  public static String getMetricsServletName(CommonMetric commonMetric) {
    Preconditions.checkNotNull(commonMetric);
    String baseName = commonMetric.getCodahaleMetric().getName();
    String nameParts[] = StringUtils.split(baseName, "_");
    for (int ii = 1; ii < nameParts.length; ii++) {
      nameParts[ii] = StringUtils.capitalize(nameParts[ii]);
    }
    return StringUtils.join(nameParts, StringUtils.EMPTY);
  }

  /**
   * Generate a list of codahale metrics for a metric set and a version using
   * the adapter to generate names and contexts. The list can then be embedded
   * in the fixture to be used by the CodahaleMetricAdapter to generate metric
   * descriptors.
   * Note that this method does not support codahale 2.x as in codahale 2.x the
   * common metrics are not exposed as real codahale metrics, i.e., they are not
   * real gauges.
   * @param metricSet
   * @param version
   * @param adapter
   * @return
   */
  public static List<CodahaleMetric> generateCodahaleMetricsForMetricSet(
      MetricSet metricSet,
      Version version,
      CommonMetricAdapter adapter) {
    Preconditions.checkNotNull(metricSet);
    Preconditions.checkNotNull(version);
    Preconditions.checkNotNull(adapter);
    Preconditions.checkArgument(
        !version.equals(Version.CODAHALE_2_X_VIRTUAL_MACHINE_METRICS));
    List<CodahaleMetric> ret = Lists.newArrayList();
    for (CommonMetric metric : metricSet.getMetrics()) {
      if (metric.isApplicableToVersion(version)) {
        ret.add(new CodahaleMetric.Builder(metric.getCodahaleMetric())
                .setName(adapter.getName(metric))
                .setContext(adapter.getContext(metric))
                .build());
      }
    }
    return ret;
  }

  /**
   * Generate a list of metric descriptors for a metric set and a version using
   * the adapter to generate names and contexts. The list can then be embedded
   * in the fixture to be used by the CodahaleMetricAdapter.
   * @param metricSet
   * @param version
   * @param adapter
   * @param serviceName
   * @return
   */
  public static List<MetricDescriptor> generateMetricDescritptorsForMetricSet(
      MetricSet metricSet,
      Version version,
      CommonMetricAdapter adapter,
      String serviceName) {
    Preconditions.checkNotNull(metricSet);
    Preconditions.checkNotNull(version);
    Preconditions.checkNotNull(adapter);
    Preconditions.checkNotNull(serviceName);
    List<MetricDescriptor> ret = Lists.newArrayList();
    for (CommonMetric metric : metricSet.getMetrics()) {
      if (metric.isApplicableToVersion(version)) {
        CodahaleMetric codahaleMetric = metric.getCodahaleMetric();
        ret.add(new MetricDescriptorImpl.Builder()
                .setName(serviceName, adapter.getName(metric))
                .setLabel(codahaleMetric.getLabel())
                .setDescription(codahaleMetric.getDescription())
                .setNumeratorUnit(codahaleMetric.getNumeratorUnit())
                .setDenominatorUnit(codahaleMetric.getDenominatorUnit())
                .setIsCounter(false)
                .setContext(adapter.getContext(metric))
                .build());
      }
    }
    return ret;
  }
}
