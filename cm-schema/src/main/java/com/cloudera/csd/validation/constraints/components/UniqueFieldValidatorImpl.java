// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints.components;

import com.cloudera.csd.validation.constraints.UniqueField;
import com.cloudera.csd.validation.constraints.UniqueFieldValidator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * The implementation of the UniqueFieldValidator
 */
public class UniqueFieldValidatorImpl implements UniqueFieldValidator {

  private UniqueField uniqueField;
  private boolean skipNulls;

  @Override
  public void initialize(UniqueField constraintAnnotation) {
    this.uniqueField = constraintAnnotation;
    this.skipNulls = this.uniqueField.skipNulls();
  }

  @Override
  public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {
    if (list != null) {
      Set<Object> seenSoFar = Sets.newHashSet();
      for (Object obj : list) {
        Object value = propertyValue(obj, uniqueField.value());
        if ((value == null) && this.skipNulls) {
          continue;
        }
        if (seenSoFar.contains(value)) {
          addViolation(context, uniqueField.value());
          return false;
        }
        seenSoFar.add(value);
      }
    }
    return true;
  }

  /**
   * Adds a violation with the field in the context.
   * @param context the context
   * @param fieldName the field name.
   */
  @VisibleForTesting
  void addViolation(ConstraintValidatorContext context, String fieldName) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
           .addPropertyNode(fieldName)
           .inIterable()
           .addConstraintViolation();
  }

  /**
   * @return return the value of the property
   */
  @VisibleForTesting
  Object propertyValue(Object obj, String property) {
    try {
      PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(obj, property);
      return desc.getReadMethod().invoke(obj, new Object[]{});
    } catch( Exception e) {
      throw new ValidationException(e);
    }
  }
}
