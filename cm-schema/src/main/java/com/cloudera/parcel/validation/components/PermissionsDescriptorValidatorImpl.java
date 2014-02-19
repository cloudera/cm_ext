// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.cloudera.validation.DescriptorValidatorImpl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class that implements the DescriptorValidator interface
 * for PermissionsDescriptor objects.
 */
public class PermissionsDescriptorValidatorImpl extends DescriptorValidatorImpl<PermissionsDescriptor> {

  @Autowired
  public PermissionsDescriptorValidatorImpl(Validator validator) {
    super(validator, "permissions");
  }
}
