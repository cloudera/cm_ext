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
 * This set of conventions is tightly coupled to the serialization format of
 * the codahale 3.0.x MetricsModule json serialization (which in turn calls the
 * different metric type serialization). This tool is intended to produce the
 * conventions fixture.
 *
 * If passed an argument, this will write the fixture to a file. If passed no
 * arguments, it simply prints to the console.
 */
public class CodahaleJson30MetricConventionGenerator {
  public static CodahaleMetricConventions makeConventions() {
    CodahaleMetricConventions conventions = new CodahaleMetricConventions();

    conventions.gaugeContextSuffix = "value";

    conventions.counterContextSuffix = "count";

    conventions.meterContextSuffixes = Maps.newTreeMap();
    conventions.meterContextSuffixes.put(
        MeterMetricType.COUNT, "count");
    conventions.meterContextSuffixes.put(
        MeterMetricType.MEAN_RATE_GAUGE, "mean_rate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.ONE_MIN_RATE_GAUGE, "m1_rate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.FIVE_MIN_RATE_GAUGE, "m5_rate");
    conventions.meterContextSuffixes.put(
        MeterMetricType.FIFTEEN_MIN_RATE_GAUGE, "m15_rate");

    conventions.timerContextSuffixes = Maps.newTreeMap();
    conventions.timerContextSuffixes.put(
        TimerMetricType.MIN, "min");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MAX, "max");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MEAN, "mean");
    conventions.timerContextSuffixes.put(
        TimerMetricType.STDDEV, "stddev");
    conventions.timerContextSuffixes.put(
        TimerMetricType.MEDIAN, "p50");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_75, "p75");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_99, "p99");
    conventions.timerContextSuffixes.put(
        TimerMetricType.PERCENTILE_999, "p999");
    conventions.timerContextSuffixes.put(
        TimerMetricType.COUNT, "count");
    conventions.timerContextSuffixes.put(
        TimerMetricType.ONE_MIN_RATE, "m1_rate");
    conventions.timerContextSuffixes.put(
        TimerMetricType.FIVE_MIN_RATE, "m5_rate");
    conventions.timerContextSuffixes.put(
        TimerMetricType.FIFTEEN_MIN_RATE, "m15_rate");

    conventions.histogramContextSuffixes = Maps.newTreeMap();
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.COUNT, "count");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MIN, "min");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MAX, "max");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MEAN, "mean");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.STDDEV, "stddev");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.MEDIAN, "p50");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_75, "p75");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_99, "p99");
    conventions.histogramContextSuffixes.put(
        HistogramMetricType.PERCENTILE_999, "p999");

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
