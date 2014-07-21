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
package com.cloudera.csd.validation.references.constraints;

import com.cloudera.csd.StringInterpolator;
import com.cloudera.csd.validation.references.DescriptorPath;
import com.cloudera.csd.validation.references.DescriptorPath.DescriptorNode;
import com.cloudera.csd.validation.references.DescriptorPath.PropertyDescriptorNode;
import com.cloudera.csd.validation.references.annotations.AvailableSubstitutions;
import com.cloudera.csd.validation.references.annotations.ReferenceType;
import com.cloudera.csd.validation.references.annotations.SubstitutionType;
import com.cloudera.csd.validation.references.components.DescriptorPathImpl.PropertyNode;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * A reference constraint that checks entity references in
 * variables that have substitutions. Any element that has the
 * {@link com.cloudera.csd.validation.references.annotations.AvailableSubstitutions}
 * annotation will be examined.
 *
 * @param <T> the type of tyhe root object.
 */
public class SubstitutionConstraint<T> extends AbstractReferenceConstraint<T> {

  private final StringInterpolator interpolator;

  private static final Joiner JOINER = Joiner.on(", ");
  private static final String ERROR_MSG = "has invalid substitutions [%s]. Substitutions available: [%s]";

  public SubstitutionConstraint(StringInterpolator interpolator) {
    this.interpolator = interpolator;
  }

  @Override
  public Class<? extends DescriptorNode> getNodeType() {
    return PropertyDescriptorNode.class;
  }

  @Override
  public Class<? extends Annotation> getAnnotationType() {
    return AvailableSubstitutions.class;
  }

  @Override
  public List<ConstraintViolation<T>> checkConstraint(Annotation annotation,
                                                      Object obj,
                                                      DescriptorPath path,
                                                      SetMultimap<ReferenceType, String> allowedRefs) {

    AvailableSubstitutions ref = (AvailableSubstitutions)annotation;
    Method method = path.getHeadNode().as(PropertyNode.class).getMethod();

    Set<SubstitutionType> types = ImmutableSet.copyOf(ref.type());
    Collection<String> templates = getIds(method, obj);

    List<ConstraintViolation<T>> errors = Lists.newArrayList();
    for (String template : templates) {
      Set<String> variables = interpolator.getVariables(template);

      Set<String> candidates = Sets.newHashSet();
      for (SubstitutionType type : types) {
        if (type == SubstitutionType.PARAMETERS) {
          candidates.addAll(allowedRefs.get(ReferenceType.PARAMETER));
        } else {
          candidates.add(type.toString().toLowerCase());
        }
      }
      Set<String> badVars = Sets.difference(variables, candidates);
      if (!badVars.isEmpty()) {
        errors.add(createViolation(template, path, badVars, candidates));
      }
    }
    return errors;
  }

  private ConstraintViolation<T> createViolation(String id,
                                                 DescriptorPath path,
                                                 Set<String> badVariables,
                                                 Set<String> candidates) {

    String msg = String.format(ERROR_MSG, JOINER.join(badVariables), JOINER.join(candidates));
    return ReferenceConstraintViolation.forViolation(msg,
            path.getHeadNode(),
            id,
            path,
            ElementType.TYPE);
  }
}
