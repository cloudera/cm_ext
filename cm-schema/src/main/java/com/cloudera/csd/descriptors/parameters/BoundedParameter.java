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
  @Expression("minValue == null or softMinValue == null or minValue < softMinValue"),
  @Expression("minValue == null or softMaxValue == null or minValue < softMaxValue"),
  @Expression("minValue == null or maxValue == null or minValue < maxValue"),
  @Expression("softMinValue == null or softMaxValue == null or softMinValue < softMaxValue"),
  @Expression("softMinValue == null or maxValue == null or softMinValue < maxValue"),
  @Expression("softMaxValue == null or maxValue == null or softMaxValue < maxValue")
})
public interface BoundedParameter<T extends Comparable<T>> extends Parameter<T> {

  /** Absolute minimum value of this parameter. */
  T getMinValue();

  /** Absolute maximum value of this parameter. */
  T getMaxValue();

  /** Recommended minimum value of this parameter. */
  T getSoftMinValue();

  /** Recommended maximum value of this parameter. */
  T getSoftMaxValue();

  /** Unit of this parameter. */
  CsdParamUnits getUnit();
}
