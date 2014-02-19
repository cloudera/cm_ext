// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.cli.validator;

import com.cloudera.cli.validator.components.DescriptorRunner;
import com.cloudera.cli.validator.components.ParcelDirectoryRunner;
import com.cloudera.cli.validator.components.ParcelFileRunner;
import com.cloudera.common.Parser;
import com.cloudera.config.DefaultValidatorConfiguration;
import com.cloudera.validation.DescriptorValidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.cloudera.cli.validator.components")
public class ApplicationConfiguration extends DefaultValidatorConfiguration {

  private <T> DescriptorRunner<T> createValidationRunner(String parserName, String validatorName) {
    @SuppressWarnings("unchecked")
    Parser<T> parser = ctx.getBean(parserName, Parser.class);

    @SuppressWarnings("unchecked")
    DescriptorValidator<T> validator = ctx.getBean(validatorName, DescriptorValidator.class);

    return new DescriptorRunner<T>(parser, validator);
  }

  @Bean
  public DescriptorRunner<?> sdlRunner() {
    return createValidationRunner("sdlParser", "serviceDescriptorValidator");
  }

  @Bean
  public DescriptorRunner<?> parcelRunner() {
    return createValidationRunner("parcelParser", "parcelDescriptorValidator");
  }

  @Bean
  public DescriptorRunner<?> alternativesRunner() {
    return createValidationRunner("alternativesParser",
                                  "alternativesDescriptorValidator");
  }

  @Bean
  public DescriptorRunner<?> permissionsRunner() {
    return createValidationRunner("permissionsParser",
                                  "permissionsDescriptorValidator");
  }

  @Bean
  public ValidationRunner parcelDirectoryRunner() {
    return new ParcelDirectoryRunner();
  }

  @Bean
  public ValidationRunner parcelFileRunner() {
    return new ParcelFileRunner();
  }
}
