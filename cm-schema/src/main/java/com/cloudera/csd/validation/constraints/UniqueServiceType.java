// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * A constraint that checks that the service type
 * does not conflict with any built-in service type in CM.
 * This is a best effor as there could be more service
 * types that conflict in CM.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueServiceTypeValidator.class })
public @interface UniqueServiceType {

  String message() default "{custom.validation.constraints.UniqueServiceType.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
