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
package com.cloudera.csd.validation.references.components;

import com.cloudera.csd.StringInterpolator;
import com.cloudera.csd.validation.references.DescriptorPath;
import com.cloudera.csd.validation.references.DescriptorPath.BeanDescriptorNode;
import com.cloudera.csd.validation.references.DescriptorPath.DescriptorNode;
import com.cloudera.csd.validation.references.DescriptorVisitor;
import com.cloudera.csd.validation.references.ReferenceConstraint;
import com.cloudera.csd.validation.references.ReferenceValidator;
import com.cloudera.csd.validation.references.annotations.IncludeAdditionalReferences;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.Referenced;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl.PropertyNode;
import com.cloudera.csd.validation.references.components.DescriptorVisitorImpl.AbstractNodeProcessor;
import com.cloudera.csd.validation.references.constraints.ReferencedEntityConstraint;
import com.cloudera.csd.validation.references.constraints.SubstitutionConstraint;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;

/**
 * The implementation to the ReferenceValidator.
 */
public class ReferenceValidatorImpl implements ReferenceValidator {

  private final DescriptorVisitor visitor;
  private final StringInterpolator interpolator;

  public ReferenceValidatorImpl(DescriptorVisitor visitor, StringInterpolator interpolator) {
    this.visitor = visitor;
    this.interpolator = interpolator;
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validate(T descriptor) {

    ReferenceCollector collector = new ReferenceCollector();
    SetMultimap<ReferenceType, DescriptorPath> references = visitor.visit(descriptor, collector);

    ConstraintViolationCollector<T> listener = new ConstraintViolationCollector<T>(references,
                                                                                   new ReferencedEntityConstraint(),
                                                                                   new SubstitutionConstraint(interpolator));
    return visitor.visit(descriptor, listener);
  }

  /**
   * A node processor that collects all the available references
   * in the descriptor. These references are fed into the reference
   * constraints to check it a reference is valid.
   */
  public static class ReferenceCollector extends AbstractNodeProcessor<SetMultimap<ReferenceType, DescriptorPath>> {
    private final SetMultimap<ReferenceType, DescriptorPath> references;

    public ReferenceCollector() {
      references = LinkedHashMultimap.create();
    }

    @Override
    public SetMultimap<ReferenceType, DescriptorPath> getResult() {
      return references;
    }

    @Override
    public void beforeNode(Object obj, DescriptorPath path) {
      if (path.getHeadNode().getKind() != ElementKind.BEAN) {
        return;
      }
      BeanDescriptorNode node = path.getHeadNode().as(BeanDescriptorNode.class);
      Named named = ReflectionHelper.findAnnotation(node.getBean().getClass(), Named.class);
      Referenced referenced = ReflectionHelper.findAnnotation(node.getBean().getClass(), Referenced.class);
      if (referenced != null) {
        if (referenced.as().equals("") && named == null) {
          throw new IllegalStateException("The @Referenced annotation requires the @Named to also exist.");
        }
        ReferenceType type = referenced.type();
        references.put(type, path.onlyInclude(ElementKind.BEAN));
      }
    }
  }

  /**
   * This class performs the actual validation using the supplied
   * reference constraints and all the valid references from the
   * {@link com.cloudera.csd.validation.references.components.ReferenceValidatorImpl.ReferenceCollector}
   * @param <T> The root object type.
   */
  public static class ConstraintViolationCollector<T> extends AbstractNodeProcessor<Set<ConstraintViolation<T>>> {
    private final SetMultimap<ReferenceType, String> allowedRefs = LinkedHashMultimap.create();
    private final SetMultimap<ReferenceType, DescriptorPath> allRefs;
    private final Set<ConstraintViolation<T>> violations = Sets.newHashSet();
    private final List<ReferenceConstraint<T>> constraints;

    public ConstraintViolationCollector(SetMultimap<ReferenceType, DescriptorPath> allRefs,
                                        ReferenceConstraint<T>... constraints) {
      this.allRefs = allRefs;
      this.constraints = ImmutableList.copyOf(constraints);
    }

    @Override
    public Set<ConstraintViolation<T>> getResult() {
      return this.violations;
    }

    @Override
    public void beforeNode(Object obj, DescriptorPath path) {
      SetMultimap<ReferenceType, String> refs = getRelatedPaths(obj, path);
      for (Map.Entry<ReferenceType, Collection<String>> entry : refs.asMap().entrySet()) {
        ReferenceType type = entry.getKey();
        allowedRefs.get(type).addAll(entry.getValue());
      }
      callReferenceConstraints(obj, path);
    }

    private SetMultimap<ReferenceType, String> getRelatedPaths(Object obj, DescriptorPath path) {
      SetMultimap<ReferenceType, String> refs = LinkedHashMultimap.create();

      String additionalScope = "";
      IncludeAdditionalReferences scope = ReflectionHelper.findAnnotation(obj.getClass(), IncludeAdditionalReferences.class);
      if (scope != null) {
        additionalScope = ReflectionHelper.propertyValueByName(obj, scope.value(), String.class);
      }

      DescriptorPath trimmedPath = path.onlyInclude(ElementKind.BEAN);
      for (Map.Entry<ReferenceType, DescriptorPath> entry : allRefs.entries()) {
        DescriptorPath refPath = entry.getValue();
        DescriptorPath prefix = refPath.removeFromHead();
        if (trimmedPath.equals(prefix) || (refPath.contains(additionalScope, ElementKind.BEAN))) {
          refs.put(entry.getKey(), refPath.getHeadNode().getName());
        }
      }
      return refs;
    }

    private void callReferenceConstraints(Object obj, DescriptorPath path) {
      DescriptorNode node = path.getHeadNode();
      for (ReferenceConstraint<T> constraint : constraints) {
        if (node.getClass().isAssignableFrom(constraint.getNodeType()))  {
          continue;
        }

        Annotation annotation;
        Class<? extends  Annotation> annClass = constraint.getAnnotationType();
        if (node.getKind() == ElementKind.BEAN) {
          annotation = ReflectionHelper.findAnnotation(obj.getClass(), annClass);
        } else if (node.getKind() == ElementKind.PROPERTY) {
          Method method = node.as(PropertyNode.class).getMethod();
          annotation = ReflectionHelper.findAnnotation(method, annClass);
        } else {
          throw new IllegalStateException(node.getKind() + " is an unsupported type.");
        }

        if (annotation == null) {
          continue;
        }

        this.violations.addAll(constraint.checkConstraint(annotation, obj, path, allowedRefs));
      }
    }

    @Override
    public void afterNode(Object obj, DescriptorPath path) {
      SetMultimap<ReferenceType, String> refs = getRelatedPaths(obj, path);
      for (Map.Entry<ReferenceType, Collection<String>> entry : refs.asMap().entrySet()) {
        ReferenceType type = entry.getKey();
        allowedRefs.get(type).removeAll(entry.getValue());
      }
    }
  }
}
