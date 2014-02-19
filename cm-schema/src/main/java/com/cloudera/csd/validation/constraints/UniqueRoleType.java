// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * A constraint that checks the role type
 * does not conflict with any built-in role types in CM.
 * This is a best effort as CM could have more role types
 * that we don't know ahead of time.
 *
 * This is a current limitation
 * and so all role types need to be scoped for the service.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueRoleTypeValidator.class })
public @interface UniqueRoleType {

  String message() default "{custom.validation.constraints.UniqueRoleType.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
