// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A constraint that indicates that a bean field needs
 * to be unique within the collection.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueFieldValidator.class })
public @interface UniqueField {

  String value();

  /**
   * If we should skip null values in the check.
   * Defaults to true.
   */
  boolean skipNulls() default true;

  String message() default "{custom.validation.constraints.UniqueField.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * Defines several {@link UniqueField} annotations on the same element.
   *
   * @see UniqueField
   */
  @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
  @Retention(RUNTIME)
  @Documented
  @interface List {

    UniqueField[] value();
  }
}
