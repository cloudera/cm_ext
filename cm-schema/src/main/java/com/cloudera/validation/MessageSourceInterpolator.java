// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.validation;

import com.google.common.annotations.VisibleForTesting;

import java.util.Locale;
import javax.validation.MessageInterpolator;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * A message interpolator that delegates to the a Spring message source to
 * do the translation.
 */
public class MessageSourceInterpolator extends DelegatingMessageInterpolator {

  private final MessageSource source;

  public MessageSourceInterpolator(MessageInterpolator delegate) {
    super(delegate);
    this.source = messageSource();
  }

  private MessageSource messageSource() {
    ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
    bean.setBasename("classpath:schema.validation");
    bean.setDefaultEncoding("UTF-8");
    return bean;
  }

  @Override
  public String doInterpolate(String messageTemplate, Context context) {
    return translate(messageTemplate);
  }

  /**
   * Returns the message from the key.
   * If it is a standard JRSR 303, or hibernate
   * constraint it converts it to our internal keys.
   *
   * @param templateKey the templateKey
   * @return translated string.
   */
  @VisibleForTesting
  String translate(String templateKey) {
    // remove wrapping {..} brackets
    if (templateKey.startsWith("{") && templateKey.endsWith("}")) {
      templateKey = templateKey.substring(1, templateKey.length() - 1);
    }

    try {
      Object[] args = {};
      return source.getMessage(templateKey, args, Locale.getDefault());
    } catch (NoSuchMessageException e) {
      return templateKey;
    }
  }
}
