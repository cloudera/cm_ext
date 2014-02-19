// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.constraints;

import java.util.Collection;
import javax.validation.ConstraintValidator;

/**
 * The interface for the UniqueField Constraint
 */
public interface UniqueFieldValidator extends ConstraintValidator<UniqueField, Collection<?>> {}
