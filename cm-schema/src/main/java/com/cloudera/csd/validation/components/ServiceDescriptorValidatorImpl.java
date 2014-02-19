// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.validation.components;

import com.cloudera.csd.descriptors.ServiceDescriptor;
import com.cloudera.validation.DescriptorValidator;
import com.cloudera.validation.DescriptorValidatorImpl;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class that implements the DescriptorValidator interface
 * for ServiceDescriptor objects.
 */
public class ServiceDescriptorValidatorImpl extends DescriptorValidatorImpl<ServiceDescriptor>
                                            implements DescriptorValidator<ServiceDescriptor> {

  @Autowired
  public ServiceDescriptorValidatorImpl(Validator validator) {
    super(validator, "service");
  }
}
