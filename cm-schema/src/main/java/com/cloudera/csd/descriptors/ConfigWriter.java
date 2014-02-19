// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.cloudera.csd.descriptors.generators.ConfigGenerator;
import com.cloudera.csd.descriptors.generators.PeerConfigGenerator;
import com.cloudera.csd.validation.constraints.UniqueField;

import java.util.Set;

public interface ConfigWriter {

  @UniqueField("filename")
  Set<ConfigGenerator> getGenerators();

  @UniqueField("filename")
  Set<PeerConfigGenerator> getPeerConfigGenerators();
}
