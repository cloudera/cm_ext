// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Describes an entry for installation with the 'alternatives' mechanism.
 */
public interface AlternativesDescriptor {

  /**
   * The logical name for the link group in alternatives. It will also serve as
   * the sub directory name within the process directory for all the generated
   * configuration files as defined by {@link #getConfigWriter()}.
   */
  @NotNull
  String getName();

  /**
   * The symbolic link to be used by clients that internally points to
   * the alternatives managed locations. The files will be deployed to a
   * subdirectory called "conf". For example, if link root is "/etc/service",
   * the complete link would be "/etc/service/conf".
   */
  @NotNull
  String getLinkRoot();

  /**
   * Default priority when the configuration directory is installed into
   * alternatives.
   *
   * @return the priority, larger value means higher precedence
   */
  @Min(0)
  long getPriority();
}
