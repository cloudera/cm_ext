// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import java.util.Set;

public interface URIParameter extends StringParameter {

  /** Allowed schemes for the URI specified by this parameter. */
  Set<String> getAllowedSchemes();

  /** Whether the URI is opague or not. */
  boolean isOpaque();
}
