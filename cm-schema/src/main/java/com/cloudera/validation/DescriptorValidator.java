// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.validation;

import java.util.Set;

/**
 * Validates a descriptor.
 */
public interface DescriptorValidator<T> {

  /**
   * Validates and returns a set of violations for
   * the service descriptor. If valid, an
   * empty set is returned.
   *
   * @param descriptor the descriptor
   * @return the list of violations, empty if valid.
   */
  Set<String> validate(T descriptor);
}
