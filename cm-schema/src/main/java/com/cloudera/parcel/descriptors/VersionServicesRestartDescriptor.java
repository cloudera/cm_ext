//  Copyright (c) 2015 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.descriptors;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Release-level restart information descriptor, that describes service types that
 * are must be restarted, when upgrading between parcels.
 */
public interface VersionServicesRestartDescriptor {

  enum Scope {
    SERVICE_ONLY,
    DEPENDENTS_ONLY,
    SERVICE_AND_DEPENDENTS
  }

  /**
   * Contains a map of service type to {@link Scope}
   * @return a map of service type to {@link Scope}
   */
  @NotNull
  Map<String, Scope> getServiceInfo();

  /**
   * Parent parcel version of this version descriptor.
   *
   * Can be null, in which case this is determined to be the oldest version
   * supported from which the restart information will be used for selective restarts.
   *
   * @return parent version, if specified.
   */
  String getParentVersion();

  /**
   * Child parcel version of this version descriptor.
   *
   * Can be null, in which case this is determined to be the newest version
   * supported from which the restart information will be used for selective restarts.
   *
   * @return child version, if specified.
   */
  String getChildVersion();
}
