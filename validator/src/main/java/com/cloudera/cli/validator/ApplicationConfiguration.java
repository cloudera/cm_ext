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
  public DescriptorRunner<?> manifestRunner() {
    return createValidationRunner("manifestParser",
                                  "manifestDescriptorValidator");
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
