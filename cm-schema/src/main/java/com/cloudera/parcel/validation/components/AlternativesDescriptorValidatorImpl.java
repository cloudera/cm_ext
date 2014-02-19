// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.validation.DescriptorValidatorImpl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class that implements the DescriptorValidator interface
 * for AlternativesDescriptor objects.
 */
public class AlternativesDescriptorValidatorImpl extends DescriptorValidatorImpl<AlternativesDescriptor> {

  @Autowired
  public AlternativesDescriptorValidatorImpl(Validator validator) {
    super(validator, "alternatives");
  }
}
