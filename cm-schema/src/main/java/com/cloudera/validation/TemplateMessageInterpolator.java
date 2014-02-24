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
package com.cloudera.validation;

import com.cloudera.csd.StringInterpolator;

import java.util.Map;

import javax.validation.MessageInterpolator;

/**
 * A Message Interpolator that replaces template variables
 * with the variables provided by the constraint annotations.
 * This includes all the attributes that are part of the annotation.
 * For example: assume we have a constraint violation on @Size
 *
 * ...
 * @Size(min=5, max=10, message="must be between ${min} and ${max}")
 * public int val;
 * ...
 *
 * The message will converted to: "must be between 5 and 10"
 * Note: nested templates are not supported.
 */
public class TemplateMessageInterpolator extends DelegatingMessageInterpolator {

  private final StringInterpolator stringInterpolator;

  public TemplateMessageInterpolator(MessageInterpolator delegate,
                                     StringInterpolator stringInterpolator) {
    super(delegate);
    this.stringInterpolator = stringInterpolator;
  }

  @Override
  public String doInterpolate(String messageTemplate, Context context) {
    // This map contains all the attributes that existed in the annotation.
    // Use use these as substitution variables.
    Map<String, Object> attributes = context.getConstraintDescriptor()
                                            .getAttributes();
    return stringInterpolator.interpolate(messageTemplate, attributes);
  }
}
