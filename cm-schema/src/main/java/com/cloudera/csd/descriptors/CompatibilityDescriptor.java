// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.csd.descriptors;

import javax.validation.constraints.Min;

/**
 * Describes the compatibility requirements for the CSD.
 */
public interface CompatibilityDescriptor {

  /**
   * A range of versions for the dependent software.
   * Both the min and max are inclusive and optional.
   *
   * TODO: Add range checks.
   */
  public interface VersionRange {
    String getMin();

    String getMax();
  }

  /**
   * Describes compatibility with other revisions of this CSD.
   * When authoring a new revision, advertise a higher generation
   * if the new revision introduces changes that are
   * incompatible with older revisions. If a generation
   * is not specified, it defaults to 1.
   */
  @Min(1)
  Long getGeneration();

  /**
   * Describes the compatible versions of the CDH cluster.
   * This is optional and if not specified, the CSD will
   * be available for all versions of CDH.
   */
  VersionRange getCdhVersion();
}
