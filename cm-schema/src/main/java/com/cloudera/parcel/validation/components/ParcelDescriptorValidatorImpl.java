// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.validation.DescriptorValidatorImpl;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class that implements the DescriptorValidator interface
 * for ParcelDescriptor objects.
 */
public class ParcelDescriptorValidatorImpl extends DescriptorValidatorImpl<ParcelDescriptor> {

  @Autowired
  public ParcelDescriptorValidatorImpl(Validator validator) {
    super(validator, "parcel");
  }
}
