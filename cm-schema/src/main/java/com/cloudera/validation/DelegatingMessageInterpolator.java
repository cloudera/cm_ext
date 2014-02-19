// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
