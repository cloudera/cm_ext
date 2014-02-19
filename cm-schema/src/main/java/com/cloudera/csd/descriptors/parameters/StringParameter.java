// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.parameters;

public interface StringParameter extends Parameter<String> {

  /** Regular expression to which this parameter's value should conform. */
  String getConformRegex();
}
