// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Constraint for a file permission in octal form.
 */
@NotNull
@Size(min = 4, max = 4)
@Pattern(regexp="[0-7][0-7][0-7][0-7]")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface FilePermission {

  String message() default "{custom.validation.constraints.FilePermission.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
