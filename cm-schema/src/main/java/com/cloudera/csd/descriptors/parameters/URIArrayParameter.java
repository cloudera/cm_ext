// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import java.util.Set;

public interface URIArrayParameter extends StringArrayParameter {

  /** Allowed schemes for the URIs specified by this parameter. */
  Set<String> getAllowedSchemes();

  /** Whether the URIs are opague or not. */
  boolean isOpaque();
}
