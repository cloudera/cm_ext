// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

import java.util.List;

public interface StringArrayParameter extends Parameter<List<String>> {

  /** Separator for items in the array. (DEFAULT is comma) .*/
  String getSeparator();

  /** Minimum number of items in the array. */
  Integer getMinLength();

  /** Maximumm number of items in the array. */
  Integer getMaxLength();
}
