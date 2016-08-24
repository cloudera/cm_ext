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
import com.cloudera.csd.tools.AbstractMetricFixtureAdapter;
import com.cloudera.csd.tools.JsonUtil;
import com.cloudera.csd.tools.JsonUtil.JsonRuntimeException;
import com.cloudera.csd.tools.MetricDescriptorImpl;
import com.cloudera.csd.tools.MetricDescriptorImpl.Builder;
import com.cloudera.csd.tools.impala.ImpalaMetricTypes.ComplexImpalaMetric;
import com.cloudera.csd.tools.impala.ImpalaMetricTypes.StatisticalMetricType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Impala metric fixture adapter. It takes as input a list of Impala metrics
 * and convert them to one or more Cloudera Manager metrics. The fixture file
 * should be a json formatted file (see Fixture class below).
 */
public class ImpalaMetricAdapter
  extends AbstractMetricFixtureAdapter<ImpalaMetric> {

  private static final Logger LOG =
      LoggerFactory.getLogger(ImpalaMetricAdapter.class);

  @Override
  public void init(String fixtureFile,
                   @Nullable String conventionsFile) throws Exception {
    Preconditions.checkNotNull(fixtureFile);

    FileInputStream in = null;
    try {
      in = FileUtils.openInputStream(new File(fixtureFile));
      fixture = JsonUtil.valueFromStream(ImpalaMetricDefinitionFixture.class,
                                         in);
    } catch (JsonRuntimeException ex) {
      LOG.error("Could not parse file at: " + fixtureFile, ex);
      throw ex;
    } finally {
      IOUtils.closeQuietly(in);
      in = null;
    }

    if (conventionsFile != null) {
      throw new UnsupportedOperationException("The Impala metrics format " +
          "does not support metric conventions.");
    }
  }

  protected Collection<? extends MetricDescriptor>
    generateMetricDescriptorsForMetric(ImpalaMetric metric) {
    Preconditions.checkNotNull(metric);
    switch (metric.getType()) {
      case GAUGE:
        return ImmutableList.of(
            new MetricDescriptorImpl.Builder()
            .setName(fixture.getServiceName(), metric.getName())
            .setLabel(metric.getLabel())
            .setDescription(metric.getDescription())
            .setNumeratorUnit(metric.getNumeratorUnit())
            .setDenominatorUnit(metric.getDenominatorUnit())
            .setIsCounter(false)
            .setContext(metric.getContext())
            .build());
      case COUNTER:
        Preconditions.checkArgument(null == metric.getDenominatorUnit(),
            "Counters should not have denominators.");
        return ImmutableList.of(
            new MetricDescriptorImpl.Builder()
            .setName(fixture.getServiceName(), metric.getName())
            .setLabel(metric.getLabel())
            .setDescription(metric.getDescription())
            .setNumeratorUnit(metric.getNumeratorUnit())
            .setIsCounter(true)
            .setContext(metric.getContext())
            .build());
      case STATISTICAL:
      {
        ImmutableList.Builder<MetricDescriptorImpl> b = ImmutableList.builder();
        for (StatisticalMetricType type : StatisticalMetricType.values()) {
          String metricName = getMetricName(metric, type);
          MetricDescriptorImpl.Builder metricBuilder =
              new MetricDescriptorImpl.Builder()
          .setName(fixture.getServiceName(), metricName)
          .setLabel(type.makeMetricLabel(metric.getLabel()))
          .setDescription(type.makeMetricDescription(metric.getDescription()))
          .setIsCounter(type.isCounter());
          determineUnitForStatisticalType(type.isCounter(), metric, metricBuilder);
          metricBuilder.setContext(String.format("%s::%s",
              metric.getContext(), type.name().toLowerCase()));
          b.add(metricBuilder.build());
        }
        return b.build();
      }
      default:
        throw new UnsupportedOperationException("Unknown impala metric type: " +
            metric.getMetricType());
    }
  }

  private String getMetricName(
      ImpalaMetric metric,
      ComplexImpalaMetric type) {
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(type);
    if (type.isCounter() &&
        null != metric.getMetricNameForCounterMetric()) {
      return metric.getMetricNameForCounterMetric();
    }
    return type.makeMetricName(metric.getName());
  }

  private void determineUnitForStatisticalType(
      boolean isCounter,
      ImpalaMetric metric,
      Builder metricBuilder) {
    Preconditions.checkNotNull(metric);
    if (isCounter) {
      Preconditions.checkNotNull(
          metric.getNumeratorForCounterMetric(),
          "A numerator unit for the counter metric of the statistical metric " +
          "must be provided");
      metricBuilder.setNumeratorUnit(metric.getNumeratorForCounterMetric());
    } else {
      metricBuilder
      .setDenominatorUnit(metric.getDenominatorUnit())
      .setNumeratorUnit(metric.getNumeratorUnit());
    }
  }
}