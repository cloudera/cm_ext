/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudera.csd.tools.codahale;

import com.cloudera.csd.tools.JsonUtil;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.HistogramMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.MeterMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.TimerMetricType;
import com.google.common.collect.Maps;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * This set of conventions is tightly coupled to the JMX bean interfaces of
 * the yammer metrics-core 2.1.3 JmxReporter. This tool is intended to
 * produce the conventions fixture.
 *
 * If passed an argument, this will write the fixture to a file. If passed no
 * arguments, it simply prints to the console. 
 */
public class CodahaleJmxMetricConventionsGenerator {

  public static CodahaleMetricConventions makeConventions() {
    CodahaleMetricConventions conventions = new CodahaleMetricConventions();

    conventions.gaugeContextSuffix = "::Value";

    conventions.counterContextSuffix = "::Count";

    conventions.meterContextSuffixes = Maps.newTreeMap();
    conventions.meterContextSuffixes.put(
        MeterMetricType.COUNT, "::Count");
    conventions.meterContextSuffixes.put(
        MeterMetricType.MEAN_RATE_GAUGE, "::MeanRate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.ONE_MIN_RATE_GAUGE, "::OneMinuteRate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.FIVE_MIN_RATE_GAUGE, "::FiveMinuteRate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.FIFTEEN_MIN_RATE_GAUGE, "::FifteenMinuteRate");

    conventions.timerContextSuffixes = Maps.newTreeMap();
    conventions.timerContextSuffixes.put(
        TimerMetricType.MIN, "::Min");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MAX, "::Max");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MEAN, "::Mean");
    conventions.timerContextSuffixes.put(
        TimerMetricType.STDDEV, "::StdDev");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MEDIAN, "::50thPercentile");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_75, "::75thPercentile");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_99, "::99thPercentile");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_999, "::999thPercentile");
    conventions.timerContextSuffixes.put(
        TimerMetricType.COUNT, "::Count");
    conventions.timerContextSuffixes.put(
        TimerMetricType.ONE_MIN_RATE, "::OneMinuteRate");
    conventions.timerContextSuffixes.put(
        TimerMetricType.FIVE_MIN_RATE, "::FiveMinuteRate");
    conventions.timerContextSuffixes.put(
        TimerMetricType.FIFTEEN_MIN_RATE, "::FifteenMinuteRate");

    conventions.histogramContextSuffixes = Maps.newTreeMap();
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.COUNT, "::Count");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MIN, "::Min");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MAX, "::Max");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MEAN, "::Mean");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.STDDEV, "::StdDev");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MEDIAN, "::50thPercentile");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_75, "::75thPercentile");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_99, "::99thPercentile");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_999, "::999thPercentile");

    return conventions;
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 1) {
      throw new RuntimeException("Unexpected arguments!");
    }
    String fixture = JsonUtil.valueAsString(makeConventions(), true);
    if (args.length == 0) {
      System.out.println(fixture);
    } else {
      FileUtils.write(new File(args[0]), fixture);
    }
  }
}
