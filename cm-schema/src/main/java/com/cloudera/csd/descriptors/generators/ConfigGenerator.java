// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.generators;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;

/** Descriptor to specify a {@link ConfigFileGenerator}. */
public interface ConfigGenerator {

  @NotBlank
  String getFilename();
  
  Set<String> getIncludedParams();
  
  Set<String> getExcludedParams();
  
  // These subclasses don't have any fields yet,
  // but we want to be consistent with how Parameters are defined.
  public interface HadoopXMLGenerator extends ConfigGenerator {
  }
  
  public interface PropertiesGenerator extends ConfigGenerator {
  }
}
