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
