// Copyright (c) 2017 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors.dependencyExtension;

import com.cloudera.csd.descriptors.InterfaceStability.Unstable;
import javax.validation.constraints.NotNull;

/**
 * Models a lineage extension, which is identified by 'extensionId : lineage'
 * in the service descriptor.
 */
@Unstable
public interface LineageExtension extends DirectoryExtension {

  /**
   * Whether the lineage collection is allowed in Single User Mode
   * (DEFAULT: false).
   */
  @NotNull
  Boolean isSingleUserModeAllowed();
}
