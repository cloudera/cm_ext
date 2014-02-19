// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
