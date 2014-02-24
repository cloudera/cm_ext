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

import java.util.Locale;
import javax.validation.MessageInterpolator;

/**
 * Implements the MessageInterpolator interface and also
 * offers the child a convenient way to delegate to the next
 * message interpolator in the chain.
 */
public abstract class DelegatingMessageInterpolator implements MessageInterpolator {

  private final MessageInterpolator delegate;

  public DelegatingMessageInterpolator(MessageInterpolator delegate) {
    this.delegate = delegate;
  }

  @Override
  public String interpolate(String messageTemplate, Context context) {
    return interpolate(messageTemplate, context, Locale.getDefault());
  }

  @Override
  public String interpolate(String messageTemplate, Context context,
      Locale locale) {
    String msg = doInterpolate(messageTemplate, context);
    return delegate != null ? delegate.interpolate(msg, context, locale) : msg;
  }

  public abstract String doInterpolate(String messageTemplate, Context context);
}
