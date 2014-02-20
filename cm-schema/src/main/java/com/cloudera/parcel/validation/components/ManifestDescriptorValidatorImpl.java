// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.validation.components;

import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.cloudera.validation.DescriptorValidatorImpl;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class that implements the DescriptorValidator interface
 * for ManifestDescriptor objects.
 */
public class ManifestDescriptorValidatorImpl extends DescriptorValidatorImpl<ManifestDescriptor> {

  @Autowired
  public ManifestDescriptorValidatorImpl(Validator validator) {
    super(validator, "manifest");
  }
}
