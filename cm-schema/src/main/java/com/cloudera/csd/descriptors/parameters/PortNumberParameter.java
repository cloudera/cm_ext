// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

public interface PortNumberParameter extends LongParameter {

  /** Whether this port is outbound. */
  boolean isOutbound();

  /** Whether zero is allowed for this port. */
  boolean isZeroAllowed();

  /** Whether -1 is allowed for this port. */
  boolean isNegativeOneAllowed();
}
