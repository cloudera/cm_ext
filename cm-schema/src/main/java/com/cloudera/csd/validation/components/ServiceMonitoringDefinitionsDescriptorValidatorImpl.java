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
package com.cloudera.csd.validation.components;

import com.cloudera.csd.descriptors.MetricDescriptor;
import com.cloudera.csd.descriptors.MetricEntityAttributeDescriptor;
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.MonitoringValidationContext;
import com.cloudera.csd.validation.monitoring.constraints.AdditionalAttributesReferToExistingAttributesValidator;
import com.cloudera.csd.validation.monitoring.constraints.AttributeNamePrefixedWithServiceNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.AttributesReferToExistingAttributesValidator;
import com.cloudera.csd.validation.monitoring.constraints.ConsistentMetricDefinitionValidator;
import com.cloudera.csd.validation.monitoring.constraints.CounterMetricNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.DenominatorValidator;
import com.cloudera.csd.validation.monitoring.constraints.EntityNamePrefixedWithServiceNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.EntityParentsReferToExistingEntitiesValidator;
import com.cloudera.csd.validation.monitoring.constraints.MetricNamePrefixedWithServiceNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesIsUniqueValidator;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesPrefixedWithServiceNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.ParentsAreReachableUsingAttributesValidator;
import com.cloudera.csd.validation.monitoring.constraints.WeightingMetricValidator;
import com.cloudera.csd.validation.references.ReferenceValidator;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.DescriptorValidatorImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * A class that implements the DescriptorValidator interface
 * for ServiceMonitoringDefinitionsDescriptor objects.
 */
public class ServiceMonitoringDefinitionsDescriptorValidatorImpl
  extends DescriptorValidatorImpl<ServiceMonitoringDefinitionsDescriptor>
  implements DescriptorValidator<ServiceMonitoringDefinitionsDescriptor> {

  private static final String NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS =
      "nameForCrossEntityAggregateMetrics";

  private static final Boolean SERVICE_NODE = true;
  private final Validator validator;
  private final ReferenceValidator refValidator;
  private final ImmutableSet<String> builtInNamesForCrossEntityAggregateMetrics;
  private final ImmutableSet<String> builtInEntityTypes;
  private final ImmutableSet<String> builtInAttributes;
  private final ImmutableSet<String> builtInRoleTypes;

  public ServiceMonitoringDefinitionsDescriptorValidatorImpl(
      Validator validator,
      ReferenceValidator refValidator,
      Set<String> builtInRoleTypes,
      Set<String> builtInNamesForCrossEntityAggregateMetrics,
      Set<String> builtInEntityTypes,
      Set<String> builtInAttributes) {
    super(validator, "service");
    Preconditions.checkNotNull(builtInRoleTypes);
    Preconditions.checkNotNull(builtInNamesForCrossEntityAggregateMetrics);
    Preconditions.checkNotNull(builtInEntityTypes);
    Preconditions.checkNotNull(builtInAttributes);
    this.validator = validator;
    this.refValidator = refValidator;
    this.builtInRoleTypes = ImmutableSet.copyOf(builtInRoleTypes);
    this.builtInNamesForCrossEntityAggregateMetrics =
        ImmutableSet.copyOf(builtInNamesForCrossEntityAggregateMetrics);
    this.builtInAttributes = ImmutableSet.copyOf(builtInAttributes);
    this.builtInEntityTypes = ImmutableSet.copyOf(builtInEntityTypes);
  }

  @Override
  public Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      getViolations(ServiceMonitoringDefinitionsDescriptor descriptor, Class<?>... groups) {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> violations =
        validator.validate(descriptor, groups);
    if (!violations.isEmpty()) {
      return violations;
    }
    violations = refValidator.validate(descriptor);
    if (!violations.isEmpty()) {
      return violations;
    }
    return validateDescriptor(descriptor);
  }

  /**
   * Validate the service monitoring definitions. If there are no validation
   * errors the returned list is empty. The validator assumes that it is called
   * after the service name has been validated.
   */
  public Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      validateDescriptor(ServiceMonitoringDefinitionsDescriptor descriptor) {
    Preconditions.checkNotNull(descriptor);
    MonitoringValidationContext context =
        new MonitoringValidationContext(descriptor);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    DescriptorPathImpl root = new DescriptorPathImpl();
    root = root.addBeanNode(descriptor);
    ret.addAll(validateMetrics(
        context,
        descriptor.getMetricDefinitions(),
        root));
    root = AbstractMonitoringValidator.getPathFromProperty(
        descriptor,
        NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
        root);
    ret.addAll(validateNameForCrossEntityAggregates(
        context,
        descriptor.getNameForCrossEntityAggregateMetrics(),
        root,
        SERVICE_NODE));
    root = root.removeFromHead();
    if (null != descriptor.getRoles()) {
      for (RoleMonitoringDefinitionsDescriptor role : descriptor.getRoles()) {
        root = root.addBeanNode(role);
        ret.addAll(validateRole(
            context,
            role,
            descriptor,
            root));
        ret.addAll(validateMetrics(
            context,
            role.getMetricDefinitions(),
            root));
        root = AbstractMonitoringValidator.getPathFromProperty(
            role,
            NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
            root);
        ret.addAll(validateNameForCrossEntityAggregates(
            context,
            role.getNameForCrossEntityAggregateMetrics(),
            root,
            !SERVICE_NODE));
        root = root.removeFromHead();
        root = root.removeFromHead();
      }
    }
    if (null != descriptor.getMetricEntityTypeDefinitions()) {
      for (MetricEntityTypeDescriptor entity :
          descriptor.getMetricEntityTypeDefinitions()) {
        root = root.addBeanNode(entity);
        ret.addAll(validateEntity(
            context,
            entity,
            descriptor,
            root));
        ret.addAll(validateMetrics(
            context,
            entity.getMetricDefinitions(),
            root));
        root = AbstractMonitoringValidator.getPathFromProperty(
            entity,
            NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
            root);
        ret.addAll(validateNameForCrossEntityAggregates(
            context,
            entity.getNameForCrossEntityAggregateMetrics(),
            root,
            !SERVICE_NODE));
        root = root.removeFromHead();
        root = root.removeFromHead();
      }
    }
    if (null != descriptor.getMetricEntityAttributeDefinitions()) {
      for (MetricEntityAttributeDescriptor attribute :
           descriptor.getMetricEntityAttributeDefinitions()) {
        root = root.addBeanNode(attribute);
        ret.addAll(validateAttribute(
            context,
            attribute,
            descriptor,
            root));
        root = root.removeFromHead();
      }
    }

    ret.addAll(validateNamesForCrossEntityAggregates(
        context,
        descriptor,
        root));

    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateRole(MonitoringValidationContext context,
                 RoleMonitoringDefinitionsDescriptor role,
                 ServiceMonitoringDefinitionsDescriptor descriptor,
                 DescriptorPathImpl path) {
  Preconditions.checkNotNull(context);
  Preconditions.checkNotNull(role);
  Preconditions.checkNotNull(descriptor);
  Preconditions.checkNotNull(path);
  Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
      Sets.newLinkedHashSet();
  for (AbstractMonitoringValidator<RoleMonitoringDefinitionsDescriptor> validator :
       ImmutableList.of(
          new AdditionalAttributesReferToExistingAttributesValidator(
              builtInAttributes))) {
      ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
        context,
        role,
        path));
    }
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateAttribute(MonitoringValidationContext context,
                      MetricEntityAttributeDescriptor attribute,
                      ServiceMonitoringDefinitionsDescriptor descriptor,
                      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(attribute);
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(path);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    for (AbstractMonitoringValidator<MetricEntityAttributeDescriptor> validator :
         ImmutableList.of(
            new AttributeNamePrefixedWithServiceNameValidator(
                builtInAttributes))) {
      ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
          context,
          attribute,
          path));
    }
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateEntity(MonitoringValidationContext context,
                   MetricEntityTypeDescriptor entity,
                   ServiceMonitoringDefinitionsDescriptor descriptor,
                   DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(entity);
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(path);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    // The built in CMSERVER entity violates a number of our rules: it has no
    // name format and no immutable attributes for one. We skip validating it.
    if (entity.getName().equals("CMSERVER")) {
      return ret;
    }
    for (AbstractMonitoringValidator<MetricEntityTypeDescriptor> validator :
         ImmutableList.of(
            new EntityNamePrefixedWithServiceNameValidator(builtInEntityTypes),
            new EntityParentsReferToExistingEntitiesValidator(
                builtInRoleTypes,
                builtInEntityTypes),
            new ParentsAreReachableUsingAttributesValidator(),
            new AttributesReferToExistingAttributesValidator(
                builtInAttributes))) {
      ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
          context,
          entity,
          path));
    }
    return ret;
  }

  private List<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateNamesForCrossEntityAggregates(
        MonitoringValidationContext context,
        ServiceMonitoringDefinitionsDescriptor descriptor,
        DescriptorPathImpl root) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(root);
    NameForCrossEntityAggregatesIsUniqueValidator validator =
        new NameForCrossEntityAggregatesIsUniqueValidator();
    return validator.validate(context, descriptor, root);
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateNameForCrossEntityAggregates(
      MonitoringValidationContext context,
      @Nullable String nameForCrossEntityAggregateMetrics,
      DescriptorPathImpl path,
      boolean serviceNode) {
    Preconditions.checkNotNull(context);
    NameForCrossEntityAggregatesPrefixedWithServiceNameValidator validator =
        new NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
            builtInNamesForCrossEntityAggregateMetrics,
            serviceNode);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
        context, nameForCrossEntityAggregateMetrics, path));
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateMetrics(
      MonitoringValidationContext context,
      @Nullable List<MetricDescriptor> metricDefinitions,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(path);
    if (null == metricDefinitions) {
      return ImmutableSet.of();
    }

    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    for (MetricDescriptor metric : metricDefinitions) {
      ret.addAll(validateMetric(context, metric, path));
    }
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateMetric(
      MonitoringValidationContext context,
      MetricDescriptor metric,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(path);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    path = path.addBeanNode(metric);
    for (AbstractMonitoringValidator<MetricDescriptor> validator :
         ImmutableList.of(
             new MetricNamePrefixedWithServiceNameValidator(),
             new CounterMetricNameValidator(),
             new DenominatorValidator(),
             new WeightingMetricValidator(),
             new ConsistentMetricDefinitionValidator())) {
      ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
          context, metric, path));
    }
    return ret;
  }
}