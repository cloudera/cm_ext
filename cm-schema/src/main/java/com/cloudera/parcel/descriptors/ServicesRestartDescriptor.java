//  Copyright (c) 2015 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Parcel-level services restart descriptor.
 * Provides information for selective restart of services on parcel upgrade/downgrade.
 *
 * If this information is missing, all services will be restarted.
 */
public interface ServicesRestartDescriptor {

  /**
   * Return a map of parcel release version to {@link VersionServicesRestartDescriptor}.
   *
   * @return  map of parcel release version to {@code VersionServicesRestartDescriptor}.
   */
  @NotNull
  Map<String, VersionServicesRestartDescriptor> getVersionInfo();
}