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
package com.cloudera.csd.tools;

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * An abstract base class for metric fixture adapters based around an
 * AbstractMetricDefinitionFixture.
 */
public abstract class AbstractMetricFixtureAdapter
  <T extends AbstractMetricDefinition> implements MetricFixtureAdapter {

  protected abstract Collection<? extends MetricDescriptor>
    generateMetricDescriptorsForMetric(T metric);

  protected MetricDefinitionFixture<T> fixture;

  public String getServiceName() {
    Preconditions.checkNotNull(fixture);
    return fixture.getServiceName();
  }

  public Collection<String> getRoleNames() {
    Preconditions.checkNotNull(fixture);
    return fixture.getRolesMetrics().keySet();
  }

  public Collection<String> getEntityNames() {
    Preconditions.checkNotNull(fixture);
    return fixture.getAdditionalServiceEntityTypesMetrics().keySet();
  }

  @Override
  public List<MetricDescriptor> getServiceMetrics() {
    Preconditions.checkState(null != fixture);
    List<MetricDescriptor> ret = Lists.newArrayList();
    for (T metric : fixture.getServiceMetrics()) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }

  @Override
  public List<MetricDescriptor> getRoleMetrics(
      String roleName) {
    Preconditions.checkNotNull(fixture);
    if (null == fixture.getRolesMetrics().get(roleName)) {
      return null;
    }
    List<MetricDescriptor> ret = Lists.newArrayList();
    for (T metric : fixture.getRolesMetrics().get(roleName)) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }

  @Override
  public List<MetricDescriptor> getEntityMetrics(
      String entityName) {
    Preconditions.checkNotNull(fixture);
    List<T> entityTypeMetrics =
        fixture.getAdditionalServiceEntityTypesMetrics().get(entityName);
    if (null == entityTypeMetrics) {
      // Not all entities need to have metrics.
      return null;
    }
    List<MetricDescriptor> ret = Lists.newArrayList();
    for (T metric : entityTypeMetrics) {
      ret.addAll(generateMetricDescriptorsForMetric(metric));
    }
    return ret;
  }


}
