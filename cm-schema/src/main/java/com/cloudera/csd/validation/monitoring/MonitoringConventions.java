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
package com.cloudera.csd.validation.monitoring;

import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;

import com.google.common.base.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * A utility class with defining our monitoring conventions  regarding metrics
 * (e.g. metric names), attributes, and types. It is used by validators to
 * validate that monitoring definitions provided conform to these conventions.
 */
public class MonitoringConventions {

  public static final String RATE_SUFFIX = "_rate";
  public static final String COUNT_SUFFIX = "_count";
  public static final String NUM_OPS_SUFFIX = "_num_ops";
  public static final String TOTAL_PREFIX = "total_";
  public static final String TOTAL_SUFFIX = "_total";

  private static final Pattern NAME_FORMAT_PATTERN =
      Pattern.compile("^[a-z]+[a-z0-9]*(_[a-z0-9]+)*$");

  /**
   * Generate the user visible name for a counter metric name. Cloudera Manager
   * exposes counters as rates and converts the original metric name, foo_bar or
   * foo_bar_count to foo_bar_rate.
   * @param isCsdMetric
   * @param originalName
   * @return
   */
  public static String convertCounterNameToUserFacingReadName(
      boolean isCsdMetric,
      String originalName) {
    Preconditions.checkNotNull(originalName);
    String mungedName = originalName;
    // We prefer "foo_rate" to "foo_num_ops_rate".
    if (mungedName.endsWith(NUM_OPS_SUFFIX)) {
      mungedName = mungedName.replace(NUM_OPS_SUFFIX, "");
    }
    // We prefer "foo_rate" to "foo_count_rate".
    if (mungedName.endsWith(COUNT_SUFFIX)) {
      mungedName = mungedName.replace(COUNT_SUFFIX, "");
    }
    // We do not want total appearing in any of our metric names. We check
    // for starting with "total_", containing "_total_" and ending with
    // "_total".
    //
    // We skip this for CSD metrics. OPSAPS-25576 tracks figuring out what to
    // do here long term.
    if (!isCsdMetric) {
      if (mungedName.contains(TOTAL_PREFIX)) {
        mungedName = mungedName.replace(TOTAL_PREFIX, "");
      }
      if (mungedName.endsWith(TOTAL_SUFFIX)) {
        mungedName = mungedName.replace(TOTAL_SUFFIX, "");
      }
    }
    return mungedName + RATE_SUFFIX;
  }

  /***
   * Returns 'true' if the metric does not end with _rate, 'false' otherwise.
   * @param metricName
   * @return
   */
  public static boolean isValidEndingForCounterMetric(String metricName) {
    Preconditions.checkNotNull(metricName);
    return !metricName.endsWith(RATE_SUFFIX);
  }

  /**
   * Returns 'true' if the metric is valid, 'false' otherwise.
   * @param metricName
   * @return
   */
  public static boolean isValidMetricNameFormat(String metricName) {
    Preconditions.checkNotNull(metricName);
       Matcher matcher = NAME_FORMAT_PATTERN.matcher(metricName);
    if (!matcher.matches()) {
      return false;
    }
    return true;
  }

  /**
   * We assume that metric names ending with _rate denote a rate metric which
   * means that they have to have a denominator. This returns 'true' if that
   * condition is valid, 'false' otherwise.
   * @param metricName
   * @param denominatorUnit
   * @return
   */
  public static boolean isValidDenominatorForMetricWithRateEnding(
      String metricName,
      @Nullable String denominatorUnit) {
    Preconditions.checkNotNull(metricName);
    if (!metricName.endsWith(RATE_SUFFIX)) {
      return true;
    }
    if (null != denominatorUnit && !denominatorUnit.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * Counters cannot have denominators.
   * @param denominatorUnit
   * @return
   */
  public static boolean isValidDenominatorForCounterMetric(
      @Nullable String denominatorUnit) {
    return null == denominatorUnit || denominatorUnit.isEmpty();
  }

  /**
   * Returns the name for cross entity aggregate metrics for a service. For
   * example, for the metric fd_open and hbase service the cross entity
   * aggregate will be fd_open_across_hbases. Note that a CSD can provide a name
   * that will be used for service cross entity aggregate metrics instead.
   * @param serviceType
   * @return
   */
  public static String getNameForServiceCrossEntityAggregateMetrics(
      String serviceType) {
    Preconditions.checkNotNull(serviceType);
    return serviceType.toLowerCase() + "s";
  }

  /**
   * Returns the name for cross entity aggregate metrics for a role. For example,
   * for the metric fd_open, echo service and echo_master_server the cross
   * entity aggregate will be fd_open_across_echo_master_servers. For the
   * metric fd_open echo service and foobar_server the cross entity aggregate
   * will be fd_open_across_echo_foobar_servers. Note that a CSD can provide a
   * name that will be used for role cross entity aggregate metrics instead.
   * @param serviceType
   * @param roleType
   * @return
   */
  public static String getNameForRoleCrossEntityAggregateMetrics(
      String serviceType,
      String roleType) {
    Preconditions.checkNotNull(serviceType);
    Preconditions.checkNotNull(roleType);
    String normalizedRoleType = roleType.toLowerCase();
    String normalizedServiceType = serviceType.toLowerCase() + "_";
    if (normalizedRoleType.startsWith(normalizedServiceType)) {
      return normalizedRoleType + "s";
    }
    return String.format("%s%ss", normalizedServiceType, normalizedRoleType);
  }

  /**
   * Returns the name for cross entity aggregate metrics for an entity. For
   * example for the metric fd_open, echo service and echo_entity_one the cross
   * entity aggregate will be fd_open_across_echo_entity_ones. For the
   * metric fd_open echo service, and foobar the cross entity aggregate
   * will be fd_open_across_echo_foobars. Note that a CSD can provide a
   * name that will be used for entity cross entity aggregate metrics instead.
   * @param entityDescriptor
   * @param serviceType
   * @return
   */
  public static String getNameForEntityTypeCrossEntityAggregateMetrics(
      MetricEntityTypeDescriptor entityDescriptor,
      String serviceType) {
    Preconditions.checkNotNull(entityDescriptor);
    Preconditions.checkNotNull(serviceType);
    String entityName = entityDescriptor.getName().toLowerCase();
    String normalizedServiceType = serviceType.toLowerCase() + "_";
    if (entityName.startsWith(normalizedServiceType)) {
      return entityName + "s";
    }
    return String.format("%s%ss", normalizedServiceType, entityName);
  }
}
