// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Describes a url that can point to an external
 * web server that will give additional status information
 * about the role.
 */
public interface RoleExternalLink {

  /**
   * The logical name for this link
   */
  @NotBlank
  String getName();

  /**
   * User friendly name for this link.
   */
  @NotBlank
  String getLabel();

  /**
   * The URL to the external site. This
   * can include all standard substitutions.
   *
   * Eg. http://${host}:${web_port}/status
   * where web_port is a parameter for this role.
   */
  @NotBlank
  String getUrl();
}
