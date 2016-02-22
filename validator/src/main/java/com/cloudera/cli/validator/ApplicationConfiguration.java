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

import com.cloudera.cli.validator.components.CommandLineOptions;
import com.cloudera.cli.validator.components.ParcelDirectoryRunner;
import com.cloudera.cli.validator.components.ParcelFileRunner;
import com.cloudera.common.Parser;
import com.cloudera.config.DefaultValidatorConfiguration;
import com.cloudera.validation.DescriptorRunner;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.ValidationRunner;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.cloudera.cli.validator.components")
public class ApplicationConfiguration extends DefaultValidatorConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);

  private <T> DescriptorRunner<T> createValidationRunner(String parserName, String validatorName) {
    @SuppressWarnings("unchecked")
    Parser<T> parser = ctx.getBean(parserName, Parser.class);

    @SuppressWarnings("unchecked")
    DescriptorValidator<T> validator =
        ctx.getBean(validatorName, DescriptorValidator.class);
    return new DescriptorRunner<T>(parser, validator);
  }

  @Override
  public Set<String> builtInServiceTypes() {
    Set<String> validServiceTypes = Sets.newHashSet(super.builtInServiceTypes());
    if (ctx.containsBean(CommandLineOptions.BEAN_NAME)) {
      CommandLineOptions cmdOptions = ctx.getBean(CommandLineOptions.BEAN_NAME,
          CommandLineOptions.class);
      String extraServiceTypeFileName = cmdOptions.getOptionValue(
          CommandLineOptions.EXTRA_SERVICE_TYPE_FILE);
      String extraServiceTypeList = cmdOptions.getOptionValue(
          CommandLineOptions.EXTRA_SERVICE_TYPES);

      if (extraServiceTypeFileName != null) {
        try {
          validServiceTypes.addAll(
              Files.readAllLines(
                  Paths.get(extraServiceTypeFileName),
                  Charset.defaultCharset()));
        } catch (IOException e) {
          LOG.error("Failed to read extra service type file: {}",
              extraServiceTypeFileName,
              e);
        }
      } else if (extraServiceTypeList != null) {
        try {
          validServiceTypes.addAll(
              Arrays.asList(extraServiceTypeList.split(" ")));
        } catch (Exception e) {
          LOG.error("Failed to parse extra service type list: {}",
              extraServiceTypeList,
              e);
        }
      }
    }
    return validServiceTypes;
  }

  @Bean
  public DescriptorRunner<?> sdlRunner() {
    return createValidationRunner(
        "sdlParser",
        "serviceDescriptorValidatorWithDependencyCheck");
  }

  @Bean
  public DescriptorRunner<?> mdlRunner() {
    return createValidationRunner(
        "mdlParser",
        "serviceMonitoringDefinitionsDescriptorValidator");
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
