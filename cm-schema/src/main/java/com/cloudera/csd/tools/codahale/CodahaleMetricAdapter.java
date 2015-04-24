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
import com.cloudera.csd.tools.JsonUtil;
import com.cloudera.csd.tools.JsonUtil.JsonRuntimeException;
import com.cloudera.csd.tools.MetricDescriptorImpl;
import com.cloudera.csd.tools.MetricDescriptorImpl.Builder;
import com.cloudera.csd.tools.MetricFixtureAdapter;
import com.cloudera.csd.tools.codahale.CodahaleMetricDefinitionFixture.CodahaleMetric;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.HistogramMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.MeterMetricType;
import com.cloudera.csd.tools.codahale.CodahaleMetricTypes.TimerMetricType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A codahale (aka yammer) metric fixture adapter. It takes as input a list of
 * codahale metrics and convert them to one or more Cloudera Manager metrics.
 * The fixture file should be a json formatted file (see Fixture class below).
 */
public class CodahaleMetricAdapter implements MetricFixtureAdapter {

  private static final Logger LOG =
      LoggerFactory.getLogger(CodahaleMetricAdapter.class);

  private CodahaleMetricDefinitionFixture fixture;
  private CodahaleMetricConventions conventions;

  @Override
  public void init(String fixtureFile,
                   @Nullable String conventionsFile) throws Exception {
    Preconditions.checkNotNull(fixtureFile);

    FileInputStream in = null;
    try {
      in = FileUtils.openInputStream(new File(fixtureFile));
      fixture = JsonUtil.valueFromStream(CodahaleMetricDefinitionFixture.class,
                                         in);
    } catch (JsonRuntimeException ex) {
      LOG.error("Could not parse file at: " + fixtureFile, ex);
      throw ex;
    } finally {
      IOUtils.closeQuietly(in);
      in = null;
    }

    if (conventionsFile == null) {
      conventions = createDefaultConventions();
    } else {
      try {
        in = FileUtils.openInputStream(new File(conventionsFile));
        conventions = JsonUtil.valueFromStream(CodahaleMetricConventions.class,
                                               in);
      } catch (JsonRuntimeException ex) {
        LOG.error("Could not parse file at: " + conventionsFile, ex);
        throw ex;
      } finally {
        IOUtils.closeQuietly(in);
      }
    }
}

  public String getServiceName() {
    Preconditions.checkNotNull(fixture);
    return fixture.getServiceName();
  }

  @Override
  public List<MetricDescriptor> getServiceMetrics() {
    Preconditions.checkState(null != fixture);
    List<MetricDescriptor> ret = Lists.newArrayList();
    for (CodahaleMetric metric : fixture.getServiceMetrics()) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }

  private Collection<? extends MetricDescriptor>
    generateMetricDescriptorsForMetric(
      CodahaleMetric metric) {
    Preconditions.checkNotNull(metric);
    switch (metric.getType()) {
      case GAUGE:
        Preconditions.checkArgument(
            null == metric.getDenominatorForRateMetrics(),
            "The denominator for rate metrics is only used for timers.");
        return ImmutableList.of(
            new MetricDescriptorImpl.Builder()
                .setName(fixture.getServiceName(), metric.getName())
                .setLabel(metric.getLabel())
                .setDescription(metric.getDescription())
                .setNumeratorUnit(metric.getNumeratorUnit())
                .setDenominatorUnit(metric.getDenominatorUnit())
                .setIsCounter(false)
                .setContext(conventions.makeGaugeContext(metric.getContext()))
                .build());
      case COUNTER:
        Preconditions.checkArgument(
            null == metric.getDenominatorForRateMetrics(),
            "The denominator for rate metrics is only used for timers.");
        Preconditions.checkArgument(null == metric.getDenominatorUnit(),
                                    "Counters should not have denominators.");
        return ImmutableList.of(
            new MetricDescriptorImpl.Builder()
                .setName(fixture.getServiceName(), metric.getName())
                .setLabel(metric.getLabel())
                .setDescription(metric.getDescription())
                .setNumeratorUnit(metric.getNumeratorUnit())
                .setIsCounter(true)
                .setContext(conventions.makeCounterContext(metric.getContext()))
                .build());
      case HISTOGRAM:
      {
        Preconditions.checkArgument(
            null == metric.getDenominatorForRateMetrics(),
            "The denominator for rate metrics is only used for timers.");
        ImmutableList.Builder<MetricDescriptorImpl> b = ImmutableList.builder();
        for (HistogramMetricType type : HistogramMetricType.values()) {
          String metricName = type.makeMetricName(metric.getName());
          MetricDescriptorImpl.Builder metricBuilder =
              new MetricDescriptorImpl.Builder()
                  .setName(fixture.getServiceName(), metricName)
                  .setLabel(type.makeMetricLabel(metric.getLabel()))
                  .setDescription(type.makeMetricDescription(
                      metric.getDescription()))
                  .setIsCounter(type.isCounter());
          determineUnitForHistogramType(type.isCounter(), metric, metricBuilder);
          metricBuilder.setContext(
              conventions.makeHistogramContext(metric.getContext(), type));
          b.add(metricBuilder.build());
        }
        return b.build();
      }
      case TIMER:
      {
        ImmutableList.Builder<MetricDescriptorImpl> b = ImmutableList.builder();
        for (TimerMetricType type : TimerMetricType.values()) {
          String metricName = type.makeMetricName(metric.getName());
          MetricDescriptorImpl.Builder metricBuilder =
              new MetricDescriptorImpl.Builder()
                  .setName(fixture.getServiceName(), metricName)
                  .setLabel(type.makeMetricLabel(metric.getLabel()))
                  .setDescription(type.makeMetricDescription(
                      metric.getDescription()))
                  .setIsCounter(type.isCounter());
          determineUnitForTimerType(type, metric, metricBuilder);
          metricBuilder.setContext(
              conventions.makeTimerContext(metric.getContext(), type));
          b.add(metricBuilder.build());
        }
        return b.build();
      }
      case METER:
      {
        Preconditions.checkArgument(
            null == metric.getDenominatorForRateMetrics(),
            "The denominator for rate metrics is only used for timers.");
        ImmutableList.Builder<MetricDescriptorImpl> b = ImmutableList.builder();
        for (MeterMetricType type : MeterMetricType.values()) {
          String metricName = type.makeMetricName(metric.getName());
          MetricDescriptorImpl.Builder metricBuilder =
              new MetricDescriptorImpl.Builder()
                  .setName(fixture.getServiceName(), metricName)
                  .setLabel(type.makeMetricLabel(metric.getLabel()))
                  .setDescription(type.makeMetricDescription(
                      metric.getDescription()))
                  .setIsCounter(type.isCounter());
          determineUnitForMeterType(type, metric, metricBuilder);
          metricBuilder.setContext(
              conventions.makeMeterContext(metric.getContext(), type));
          b.add(metricBuilder.build());
        }
        return b.build();
      }
      default:
        throw new UnsupportedOperationException("Unknown codahale type: " +
                                                metric.getMetricType());
    }
  }

  private void determineUnitForMeterType(MeterMetricType type,
                                         CodahaleMetric metric,
                                         Builder metricBuilder) {
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(metricBuilder);

    metricBuilder.setNumeratorUnit(metric.getNumeratorUnit());
    if (type.isRate()) {
      Preconditions.checkNotNull(metric.getDenominatorUnit());
      metricBuilder.setDenominatorUnit(metric.getDenominatorUnit());
    }
  }

  private void determineUnitForTimerType(TimerMetricType type,
                                         CodahaleMetric metric,
                                         Builder metricBuilder) {
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(metricBuilder);

    if (type.isCounter()) {
      Preconditions.checkNotNull(
          metric.getNumeratorForCounterMetric(),
          "A numerator unit for the counter metric of the timer must be " +
          " provided");
      metricBuilder.setNumeratorUnit(metric.getNumeratorForCounterMetric());
    } else if (type.isRate()) {
      Preconditions.checkNotNull(
          metric.getNumeratorForCounterMetric(),
          "A denominator unit for the rate metrics of the timer must be " +
          "provided");
      Preconditions.checkNotNull(metric.getDenominatorForRateMetrics());
      metricBuilder
          .setDenominatorUnit(metric.getDenominatorForRateMetrics())
          .setNumeratorUnit(metric.getNumeratorForCounterMetric());
    } else {
      metricBuilder
          .setDenominatorUnit(metric.getDenominatorUnit())
          .setNumeratorUnit(metric.getNumeratorUnit());
    }
  }

  private void determineUnitForHistogramType(boolean isCounter,
                                             CodahaleMetric metric,
                                             Builder metricBuilder) {
    Preconditions.checkNotNull(metric);
    if (isCounter) {
      Preconditions.checkNotNull(
          metric.getNumeratorForCounterMetric(),
          "A numerator unit for the counter metric of the histogram must be " +
          "provided");
      metricBuilder.setNumeratorUnit(metric.getNumeratorForCounterMetric());
    } else {
      metricBuilder
          .setDenominatorUnit(metric.getDenominatorUnit())
          .setNumeratorUnit(metric.getNumeratorUnit());
    }
  }

  public Collection<String> getRoleNames() {
    Preconditions.checkNotNull(fixture);
    return fixture.getRolesMetrics().keySet();
  }

  @Override
  public List<MetricDescriptor> getRoleMetrics(
      String roleName) {
    Preconditions.checkNotNull(fixture);

    if (null == fixture.getRolesMetrics().get(roleName)) {
      return null;
    }

    List<MetricDescriptor> ret = Lists.newArrayList();
    for (CodahaleMetric metric : fixture.getRolesMetrics().get(roleName)) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }

  public Collection<String> getEntityNames() {
    Preconditions.checkNotNull(fixture);
    return fixture.getAdditionalServiceEntityTypesMetrics().keySet();
  }

  @Override
  public List<MetricDescriptor> getEntityMetrics(
      String entityName) {
    Preconditions.checkNotNull(fixture);

    if (null == fixture.getAdditionalServiceEntityTypesMetrics().get(
          entityName)) {
      // Not all entities need to have metrics.
      return null;
    }

    List<MetricDescriptor> ret = Lists.newArrayList();
    for (CodahaleMetric metric :
         fixture.getAdditionalServiceEntityTypesMetrics().get(entityName)) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }

  /**
   * The default metric conventions do nothing.
   * @return
   */
  private CodahaleMetricConventions createDefaultConventions() {
    CodahaleMetricConventions conventions = new CodahaleMetricConventions();
    conventions.meterContextSuffixes = Maps.newHashMap();
    conventions.timerContextSuffixes = Maps.newHashMap();
    conventions.histogramContextSuffixes = Maps.newHashMap();
    return conventions;
  }
}
