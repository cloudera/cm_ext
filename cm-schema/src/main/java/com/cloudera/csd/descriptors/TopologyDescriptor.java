// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import javax.validation.constraints.Min;

/**
 * Describes where this role can be deployed on
 * the cluster. This includes the number of instances,
 * and restrictions on co-location. If the topology
 * descriptor is not specified, then min instances are
 * set 1 and max instances are Integer.MAX_VALUE.
 *
 * TODO: Add validation on descriptor to check ranges
 * Need to make sure min < max
 */
public interface TopologyDescriptor {

  /** Defaults to 1 */
  @Min(0)
  Integer getMinInstances();

  /** Defaults to Integer.MAX_VALUE */
  @Min(1)
  Integer getMaxInstances();
}
