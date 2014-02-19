// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import com.fasterxml.jackson.annotation.JsonValue;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a service command used by the CSD framework.
 */
public interface ServiceCommandDescriptor {

  enum RunMode {
    /**
     * Run the role command on all roles of the applicable role type.
     */
    ALL,
    /**
     * Run the role command on one arbitrarily chosen role of the role type.
     */
    SINGLE;

    @JsonValue
    public String toJson() {
      return name().toLowerCase();
    }
  }

  @NotBlank
  String getName();

  @NotBlank
  String getLabel();

  @NotBlank
  String getDescription();

  @NotBlank
  String getRoleName();

  @NotBlank
  String getRoleCommand();

  RunMode getRunMode();
}
