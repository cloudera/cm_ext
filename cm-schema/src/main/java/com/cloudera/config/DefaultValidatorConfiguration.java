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
package com.cloudera.config;

import com.cloudera.common.Parser;
import com.cloudera.csd.StringInterpolator;
import com.cloudera.csd.components.JsonSdlParser;
import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.csd.validation.components.ServiceDescriptorValidatorImpl;
import com.cloudera.csd.validation.constraints.AutoConfigSharesValidValidator;
import com.cloudera.csd.validation.constraints.ExpressionValidator;
import com.cloudera.csd.validation.constraints.UniqueFieldValidator;
import com.cloudera.csd.validation.constraints.UniqueRoleTypeValidator;
import com.cloudera.csd.validation.constraints.UniqueServiceTypeValidator;
import com.cloudera.csd.validation.constraints.ValidServiceDependencyValidator;
import com.cloudera.csd.validation.constraints.components.AutoConfigSharesValidValidatorImpl;
import com.cloudera.csd.validation.constraints.components.ExpressionValidatorImpl;
import com.cloudera.csd.validation.constraints.components.UniqueFieldValidatorImpl;
import com.cloudera.csd.validation.constraints.components.UniqueRoleTypeValidatorImpl;
import com.cloudera.csd.validation.constraints.components.UniqueServiceTypeValidatorImpl;
import com.cloudera.csd.validation.constraints.components.ValidServiceDependencyValidatorImpl;
import com.cloudera.csd.validation.references.DescriptorVisitor;
import com.cloudera.csd.validation.references.ReferenceValidator;
import com.cloudera.csd.validation.references.components.DescriptorVisitorImpl;
import com.cloudera.csd.validation.references.components.ReferenceValidatorImpl;
import com.cloudera.parcel.components.JsonAlternativesParser;
import com.cloudera.parcel.components.JsonManifestParser;
import com.cloudera.parcel.components.JsonParcelParser;
import com.cloudera.parcel.components.JsonPermissionsParser;
import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.cloudera.parcel.validation.components.AlternativesDescriptorValidatorImpl;
import com.cloudera.parcel.validation.components.ManifestDescriptorValidatorImpl;
import com.cloudera.parcel.validation.components.ParcelDescriptorValidatorImpl;
import com.cloudera.parcel.validation.components.PermissionsDescriptorValidatorImpl;
import com.cloudera.validation.BeanConstraintValidatorFactory;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.MessageSourceInterpolator;
import com.cloudera.validation.TemplateMessageInterpolator;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Constructs all the required spring components.
 */
@Configuration
public class DefaultValidatorConfiguration {

  @Autowired
  protected ApplicationContext ctx;

  @Autowired
  protected ConfigurableListableBeanFactory beanFactory;

  public static final String BUILTIN_SERVICE_TYPES_BEAN_NAME = "builtInServiceTypes";
  public static final String BUILTIN_ROLE_TYPES_BEAN_NAME = "builtInRoleTypes";

  @Bean(name = BUILTIN_SERVICE_TYPES_BEAN_NAME)
  public Set<String> builtInServiceTypes() {
    return ImmutableSet.of(
      "HDFS",
      "MAPREDUCE",
      "HBASE",
      "OOZIE",
      "ZOOKEEPER",
      "HUE",
      "YARN",
      "IMPALA",
      "FLUME",
      "HIVE",
      "SOLR",
      "SQOOP",
      "KS_INDEXER",
      "SENTRY"
    );
  }

  @Bean(name = BUILTIN_ROLE_TYPES_BEAN_NAME)
  public Set<String> builtInRoleTypes() {
    return ImmutableSet.of(
      "ACTIVITYMONITOR",
      "AGENT",
      "ALERTPUBLISHER",
      "BALANCER",
      "BEESWAX_SERVER",
      "CATALOGSERVER",
      "DATANODE",
      "EVENTSERVER",
      "FAILOVERCONTROLLER",
      "GATEWAY",
      "HBASERESTSERVER",
      "HBASETHRIFTSERVER",
      "HBASE_INDEXER",
      "HIVEMETASTORE",
      "HIVESERVER2",
      "HOSTMONITOR",
      "HTTPFS",
      "HUE_SERVER",
      "IMPALAD",
      "JOBHISTORY",
      "JOBSUBD",
      "JOBTRACKER",
      "JOURNALNODE",
      "KT_RENEWER",
      "LLAMA",
      "MASTER",
      "NAMENODE",
      "NAVIGATOR",
      "NAVIGATORMETASERVER",
      "NFSGATEWAY",
      "NODEMANAGER",
      "OOZIE_SERVER",
      "REGIONSERVER",
      "REPORTSMANAGER",
      "RESOURCEMANAGER",
      "SECONDARYNAMENODE",
      "SERVER",
      "SERVICEMONITOR",
      "SOLR_SERVER",
      "SQOOP_SERVER",
      "STATESTORE",
      "TASKTRACKER",
      "WEBHCAT",
      "SENTRY_SERVER"
    );
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public UniqueFieldValidator uniqueFieldValidator() {
    return new UniqueFieldValidatorImpl();
  }

  @SuppressWarnings("unchecked")
  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public ValidServiceDependencyValidator validServiceDependencyValidator() {
    Set<String> validServiceTypes = (Set<String>)ctx.getBean(BUILTIN_SERVICE_TYPES_BEAN_NAME);
    return new ValidServiceDependencyValidatorImpl(validServiceTypes);
  }

  @SuppressWarnings("unchecked")
  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public UniqueServiceTypeValidator uniqueServiceTypeValidator() {
    Set<String> serviceTypes = (Set<String>)ctx.getBean(BUILTIN_SERVICE_TYPES_BEAN_NAME);
    return new UniqueServiceTypeValidatorImpl(serviceTypes);
  }

  @SuppressWarnings("unchecked")
  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public UniqueRoleTypeValidator uniqueRoleTypeValidator() {
    Set<String> roleTypes = (Set<String>)ctx.getBean(BUILTIN_ROLE_TYPES_BEAN_NAME);
    return new UniqueRoleTypeValidatorImpl(roleTypes);
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public ExpressionValidator expressionValidator() {
    return new ExpressionValidatorImpl();
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public AutoConfigSharesValidValidator autoConfigSharesValidValidator() {
    return new AutoConfigSharesValidValidatorImpl();
  }

  public MessageInterpolator defaultInterpolator() {
    return Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
  }

  public TemplateMessageInterpolator templateMessageInterpolator() {
    return new TemplateMessageInterpolator(defaultInterpolator(), stringInterpolator());
  }

  @Bean
  public MessageInterpolator messageInterpolator() {
    return new MessageSourceInterpolator(templateMessageInterpolator());
  }

  @Bean
  public Parser<ServiceDescriptor> sdlParser() {
    return new JsonSdlParser();
  }

  @Bean
  public Parser<ParcelDescriptor> parcelParser() {
    return new JsonParcelParser();
  }

  @Bean
  public Parser<AlternativesDescriptor> alternativesParser() {
    return new JsonAlternativesParser();
  }

  @Bean
  public Parser<PermissionsDescriptor> permissionsParser() {
    return new JsonPermissionsParser();
  }

  @Bean
  public Parser<ManifestDescriptor> manifestParser() {
    return new JsonManifestParser();
  }

  @Bean
  public StringInterpolator stringInterpolator() {
    return new StringInterpolator();
  }

  @Bean
  public DescriptorValidator<ServiceDescriptor> serviceDescriptorValidator() {
    Validator validator = ctx.getBean(Validator.class);
    ReferenceValidator referenceValidator = ctx.getBean(ReferenceValidator.class);
    return new ServiceDescriptorValidatorImpl(validator, referenceValidator);
  }

  @Bean
  public DescriptorVisitor descriptorVisitor() {
    return new DescriptorVisitorImpl();
  }

  @Bean
  public ReferenceValidator referenceValidator() {
    DescriptorVisitor visitor = ctx.getBean(DescriptorVisitor.class);
    StringInterpolator interpolator = ctx.getBean(StringInterpolator.class);
    return new ReferenceValidatorImpl(visitor, interpolator);
  }

  @Bean
  public DescriptorValidator<ParcelDescriptor> parcelDescriptorValidator() {
    Validator validator = ctx.getBean(Validator.class);
    return new ParcelDescriptorValidatorImpl(validator);
  }

  @Bean
  public DescriptorValidator<AlternativesDescriptor> alternativesDescriptorValidator() {
    Validator validator = ctx.getBean(Validator.class);
    return new AlternativesDescriptorValidatorImpl(validator);
  }

  @Bean
  public DescriptorValidator<PermissionsDescriptor> permissionsDescriptorValidator() {
    Validator validator = ctx.getBean(Validator.class);
    return new PermissionsDescriptorValidatorImpl(validator);
  }

  @Bean
  public DescriptorValidator<ManifestDescriptor> manifestDescriptorValidator() {
    Validator validator = ctx.getBean(Validator.class);
    return new ManifestDescriptorValidatorImpl(validator);
  }

  @Bean
  public DefaultValidatorConfiguration defaultValidatorConfiguration() {
    return new DefaultValidatorConfiguration();
  }

  @Bean
  public BeanConstraintValidatorFactory springConstraintValidatorFactory() {
    return new BeanConstraintValidatorFactory(beanFactory);
  }

  /**
   * Creates the validator factory bean that Spring uses to
   * construct a Validator.
   *
   * @return a Validator Factory Bean
   */
  @Bean
  public LocalValidatorFactoryBean validatorFactoryBean() {
    BeanConstraintValidatorFactory validatorFactory = ctx.getBean(BeanConstraintValidatorFactory.class);
    MessageInterpolator messageInterpolator = ctx.getBean(MessageInterpolator.class);
    LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
    factoryBean.setMessageInterpolator(messageInterpolator);
    factoryBean.setConstraintValidatorFactory(validatorFactory);
    return factoryBean;
  }
}
