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
package com.cloudera.csd.descriptors.parameters;

import com.cloudera.csd.validation.constraints.Expression;

@Expression.List({
  @Expression("min == null or softMin == null or min < softMin"),
  @Expression("min == null or softMax == null or min < softMax"),
  @Expression("min == null or max == null or min < max"),
  @Expression("softMin == null or softMax == null or softMin < softMax"),
  @Expression("softMin == null or max == null or softMin < max"),
  @Expression("softMax == null or max == null or softMax < max")
})
public interface BoundedParameter<T extends Comparable<T>> extends Parameter<T> {

  /** Absolute minimum value of this parameter. */
  T getMin();

  /** Absolute maximum value of this parameter. */
  T getMax();

  /** Recommended minimum value of this parameter. */
  T getSoftMin();

  /** Recommended maximum value of this parameter. */
  T getSoftMax();

  /** Unit of this parameter. */
  CsdParamUnits getUnit();
}
