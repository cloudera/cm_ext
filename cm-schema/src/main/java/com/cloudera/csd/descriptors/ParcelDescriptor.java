// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * Describes parcel specific information.
 */
public interface ParcelDescriptor {

  /**
   * THe user can add a parcel repo URL. If one is specified,
   * it will be added to the remote repository urls so that the
   * associated parcel can show up in CM.
   */
  @URL
  String getRepoUrl();

  /**
   * Lists the parcels that this service is requesting. The
   * parcels in this list need to be present for this service
   * to start. Also, If any of the parcels changes, we notify
   * the user that this service also needs to be restarted.
   */
  @NotEmpty
  Set<String> getRequiredTags();

  /**
   * Similar to required tags in that they specify a relationship
   * between this service and parcels but these parcels are not
   * required to be present. If they are, we notify the user to restart
   * this service if the parcels with these tags change.
   */
  Set<String> getOptionalTags();
}
