// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * A constraint that checks that entity types
 * are not empty and only consist of uppercase letters,
 * numbers and underscores.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Pattern(regexp="[A-Z|_|0-9]*")
@NotEmpty
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface EntityTypeFormat {

  String message() default "{custom.validation.constraints.EntityTypeFormat.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
