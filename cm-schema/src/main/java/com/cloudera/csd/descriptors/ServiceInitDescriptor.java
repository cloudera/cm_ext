// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/** 
 * Descriptor to specify the first run workflow of the service.
 * Contains the sequence of commands to run before and after
 * the first time the service is started. Only service commands
 * are allowed in initialization.
 */
public interface ServiceInitDescriptor {

  public interface ServiceInitStep {
    
    @NotBlank
    String getCommandName();
    
    boolean isFailureAllowed();
  }
  
  List<ServiceInitStep> getPreStartSteps();
  
  List<ServiceInitStep> getPostStartSteps();
}
