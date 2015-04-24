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
import com.cloudera.csd.descriptors.MetricEntityTypeDescriptor;
import com.cloudera.csd.descriptors.RoleMonitoringDefinitionsDescriptor;
import com.cloudera.csd.descriptors.ServiceMonitoringDefinitionsDescriptor;
import com.cloudera.csd.validation.monitoring.AbstractMonitoringValidator;
import com.cloudera.csd.validation.monitoring.constraints.CounterMetricNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.DenominatorValidator;
import com.cloudera.csd.validation.monitoring.constraints.MetricNamePrefixedWithServiceNameValidator;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesIsUniqueValidator;
import com.cloudera.csd.validation.monitoring.constraints.NameForCrossEntityAggregatesPrefixedWithServiceNameValidator;
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

  public ServiceMonitoringDefinitionsDescriptorValidatorImpl(
      Validator validator,
      ReferenceValidator refValidator,
      Set<String> builtInNamesForCrossEntityAggregateMetrics) {
    super(validator, "service");
    Preconditions.checkNotNull(builtInNamesForCrossEntityAggregateMetrics);
    this.validator = validator;
    this.refValidator = refValidator;
    this.builtInNamesForCrossEntityAggregateMetrics =
        ImmutableSet.copyOf(builtInNamesForCrossEntityAggregateMetrics);
  }

  @Override
  public Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      getViolations(ServiceMonitoringDefinitionsDescriptor descriptor) {
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> violations =
        validator.validate(descriptor);
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
   * @param descriptor
   * @return
   */
  public Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
      validateDescriptor(ServiceMonitoringDefinitionsDescriptor descriptor) {
    Preconditions.checkNotNull(descriptor);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    DescriptorPathImpl root = new DescriptorPathImpl();
    root = root.addBeanNode(descriptor);
    ret.addAll(validateMetrics(descriptor.getMetricDefinitions(), descriptor, root));
    root = AbstractMonitoringValidator.getPathFromProperty(
        descriptor,
        NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
        root);
    ret.addAll(validateNameForCrossEntityAggregates(
        descriptor.getNameForCrossEntityAggregateMetrics(),
        descriptor,
        root,
        SERVICE_NODE));
    root = root.removeFromHead();
    if (null != descriptor.getRoles()) {
      for (RoleMonitoringDefinitionsDescriptor role : descriptor.getRoles()) {
        root = root.addBeanNode(role);
        ret.addAll(validateMetrics(role.getMetricDefinitions(), descriptor, root));
        root = AbstractMonitoringValidator.getPathFromProperty(
            role,
            NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
            root);
        ret.addAll(validateNameForCrossEntityAggregates(
            role.getNameForCrossEntityAggregateMetrics(),
            descriptor,
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
        ret.addAll(validateMetrics(entity.getMetricDefinitions(), descriptor, root));
        root = AbstractMonitoringValidator.getPathFromProperty(
            entity,
            NAME_FOR_CROSS_ENTITY_AGGREGATE_METRICS,
            root);
        ret.addAll(validateNameForCrossEntityAggregates(
            entity.getNameForCrossEntityAggregateMetrics(),
            descriptor,
            root,
            !SERVICE_NODE));
        root = root.removeFromHead();
        root = root.removeFromHead();
      }
    }

    ret.addAll(validateNamesForCrossEntityAggregates(descriptor, root));
    return ret;
  }

  private List<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateNamesForCrossEntityAggregates(
      ServiceMonitoringDefinitionsDescriptor descriptor,
      DescriptorPathImpl root) {
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(root);
    NameForCrossEntityAggregatesIsUniqueValidator validator =
        new NameForCrossEntityAggregatesIsUniqueValidator(descriptor);
    return validator.validate(descriptor, root);
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateNameForCrossEntityAggregates(
      @Nullable String nameForCrossEntityAggregateMetrics,
      ServiceMonitoringDefinitionsDescriptor descriptor,
      DescriptorPathImpl path,
      boolean serviceNode) {
    Preconditions.checkNotNull(descriptor);
    NameForCrossEntityAggregatesPrefixedWithServiceNameValidator validator =
        new NameForCrossEntityAggregatesPrefixedWithServiceNameValidator(
            descriptor,
            builtInNamesForCrossEntityAggregateMetrics,
            serviceNode);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
        nameForCrossEntityAggregateMetrics, path));
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateMetrics(
      @Nullable List<MetricDescriptor> metricDefinitions,
      ServiceMonitoringDefinitionsDescriptor descriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(path);
    if (null == metricDefinitions) {
      return ImmutableSet.of();
    }

    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    for (MetricDescriptor metric : metricDefinitions) {
      ret.addAll(validateMetric(metric, descriptor, path));
    }
    return ret;
  }

  private Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>>
    validateMetric(
      MetricDescriptor metric,
      ServiceMonitoringDefinitionsDescriptor descriptor,
      DescriptorPathImpl path) {
    Preconditions.checkNotNull(metric);
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(path);
    Set<ConstraintViolation<ServiceMonitoringDefinitionsDescriptor>> ret =
        Sets.newLinkedHashSet();
    path = path.addBeanNode(metric);
    for (AbstractMonitoringValidator<MetricDescriptor> validator :
        ImmutableList.of(
            new MetricNamePrefixedWithServiceNameValidator(descriptor),
            new CounterMetricNameValidator(descriptor),
            new DenominatorValidator(descriptor),
            new WeightingMetricValidator(descriptor))) {
      ret.addAll(validator.<ServiceMonitoringDefinitionsDescriptor>validate(
          metric, path));
    }
    return ret;
  }
}