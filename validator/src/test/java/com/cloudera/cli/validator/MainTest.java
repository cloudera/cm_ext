// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.cli.validator;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

  private Main app;
  private String appName = "myapp";

  private String goodSdl = "src/test/resources/service_good.sdl";
  private String badSdl = "src/test/resources/service_bad.sdl";
  private String goodParcel = "src/test/resources/good_parcel.json";
  private String badParcel = "src/test/resources/bad_parcel.json";
  private String badParseParcel = "src/test/resources/bad_parse_parcel.json";
  private String goodAlternatives = "src/test/resources/good_alternatives.json";
  private String badAlternatives = "src/test/resources/bad_alternatives.json";
  private String goodPermissions = "src/test/resources/good_permissions.json";
  private String badPermissions = "src/test/resources/bad_octal_permissions.json";

  private ByteArrayOutputStream out;
  private ByteArrayOutputStream err;

  @Before
  public void setup() {
    out = new ByteArrayOutputStream();
    err = new ByteArrayOutputStream();
    app = new Main(appName, out, err);
  }

  @Test
  public void testGoodSdl() throws Exception {
    String[] args = {"-s", goodSdl};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodSdl));
  }

  @Test
  public void testBadSdl() throws Exception {
    String[] args = {"-s", badSdl};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testGoodParcel() throws Exception {
    String[] args = {"-p", goodParcel};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodParcel));
  }

  @Test
  public void testBadParcel() throws Exception {
    String[] args = {"-p", badParcel};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testBadParseParcel() throws Exception {
    String[] args = {"-p", badParseParcel};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testGoodAlternatives() throws Exception {
    String[] args = {"-a", goodAlternatives};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodAlternatives));
  }

  @Test
  public void testBadAlternatives() throws Exception {
    String[] args = {"-a", badAlternatives};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testGoodPermissions() throws Exception {
    String[] args = {"-r", goodPermissions};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodPermissions));
  }

  @Test
  public void testBadPermissions() throws Exception {
    String[] args = {"-r", badPermissions};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testMissingParcelDirectory() throws Exception {
    String[] args = {"-d", "foobar"};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("does not exist"));
  }

  @Test
  public void testMissingParcelFile() throws Exception {
    String[] args = {"-f", "foobar"};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("does not exist"));
  }

  @Test
  public void testNoArg() throws Exception {
    String[] args = {};
    app.run(args);
    assertEquals("", out.toString());
    assertTrue(err.toString().contains("usage"));
  }

  @Test
  public void testBadArg() throws Exception {
    String[] args = {"-x", badSdl};
    app.run(args);
    assertEquals("", out.toString());
    assertTrue(err.toString().contains("Unrecognized"));
  }

}
