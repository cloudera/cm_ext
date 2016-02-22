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

import com.cloudera.csd.tools.MetricDefinitionFixture;

/**
 * A class that defines the metadata required for codahale metrics in order to
 * transform them to ServiceMonitoringDefinitions. Each codahale metric will be
 * transformed to one or more Cloudera Manager MetricDescriptor. For example,
 * a codahale histogram will be transformed to several MetricDescriptors for the
 * min, max, 99 percentile etc.
 */
public class CodahaleMetricDefinitionFixture
  extends MetricDefinitionFixture<CodahaleMetric> {
}
