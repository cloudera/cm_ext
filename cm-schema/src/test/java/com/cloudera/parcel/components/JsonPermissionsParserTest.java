// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import static org.junit.Assert.*;

import com.cloudera.parcel.descriptors.PermissionDescriptor;
import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.cloudera.parcel.validation.ParcelTestUtils;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class JsonPermissionsParserTest {

  private JsonPermissionsParser parser = new JsonPermissionsParser();

  @Test
  public void testParseGoodFile() throws IOException {
    PermissionsDescriptor descriptor = parser.parse(ParcelTestUtils.getParcelJson("good_permissions.json"));
    Map<String, PermissionDescriptor> permissions = descriptor.getPermissions();
    assertEquals(2, permissions.size());

    PermissionDescriptor tc = permissions.get("lib/hadoop-0.20-mapreduce/sbin/Linux-amd64-64/task-controller");
    assertNotNull(tc);
    assertEquals("root", tc.getUser());
    assertEquals("mapred", tc.getGroup());
    assertEquals("4754", tc.getPermissions());

    PermissionDescriptor ce = permissions.get("lib/hadoop-yarn/bin/container-executor");
    assertNotNull(ce);
    assertEquals("root", ce.getUser());
    assertEquals("yarn", ce.getGroup());
    assertEquals("6050", ce.getPermissions());
  }

  @Test
  public void testParseEmptyFile() throws IOException {
    parser.parse(ParcelTestUtils.getParcelJson("empty.json"));
  }
}
