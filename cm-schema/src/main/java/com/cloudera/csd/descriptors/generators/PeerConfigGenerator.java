// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.generators;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

//TODO: check that this config generator is not present at the service level
/** a descriptor to get peer parameter configurations **/
public interface PeerConfigGenerator {

  /** The file name of the config file that will be written **/
  @NotBlank
  String getFilename();

  /** The parameters to include from each peer **/
  @NotEmpty
  Set<String> getParams();

  /** specifies which role type to use, be default it uses the current role type **/
  //TODO: Add validation that this rolename is valid.
  String getRoleName();
}
