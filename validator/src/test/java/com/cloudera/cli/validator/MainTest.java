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
  private String goodManifest = "src/test/resources/good_manifest.json";
  private String badManifest = "src/test/resources/bad_manifest.json";
  private String goodParcelDir = "src/test/resources/CDH-5.0.0-0.cdh5b2.p0.282";
  private String badParcelDir = "src/test/resources/CDH-5.0.0-0.cdh5b2.p0.281";
  private String goodParcelFile = "src/test/resources/CDH-5.0.0-0.cdh5b2.p0.282-wheezy.parcel";
  private String badParcelFile = "src/test/resources/CDH-5.0.0-0.cdh5b2.p0.281-gentoo.parcel";
  private String wrongParcelFile = "src/test/resources/CDH-5.0.0-0.cdh5b2.p0.280-wheezy.parcel";

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
  public void testGoodManifest() throws Exception {
    String[] args = {"-m", goodManifest};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodManifest));
  }

  @Test
  public void testBadManifest() throws Exception {
    String[] args = {"-m", badManifest};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("==>"));
  }

  @Test
  public void testGoodParcelDirectory() throws Exception {
    String[] args = {"-d", goodParcelDir};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodParcelDir));
    assertOccurences(out.toString(), "Validating:", 4);
  }

  @Test
  public void testBadParcelDirectory() throws Exception {
    String[] args = {"-d", badParcelDir};
    app.run(args);
    assertEquals("", err.toString());
    assertOccurences(out.toString(), "==>", 6);
    assertOccurences(out.toString(), "does not exist", 5);
    assertOccurences(out.toString(), "Validating:", 4);
    assertTrue(out.toString().contains("must be named"));
  }

  @Test
  public void testMissingParcelDirectory() throws Exception {
    String[] args = {"-d", "foobar"};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("does not exist"));
  }

  @Test
  public void testGoodParcelFile() throws Exception {
    String[] args = {"-f", goodParcelFile};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains(goodParcelFile));
    assertOccurences(out.toString(), "Validating:", 4);
  }

  @Test
  public void testBadParcelFile() throws Exception {
    String[] args = {"-f", badParcelFile};
    app.run(args);
    assertEquals("", err.toString());
    assertOccurences(out.toString(), "==>", 7);
    assertOccurences(out.toString(), "does not exist", 5);
    assertOccurences(out.toString(), "Validating:", 4);
    assertTrue(out.toString().contains("must be named"));
    assertTrue(out.toString().contains("does not appear"));
  }

  @Test
  public void testWrongParcelFilename() throws Exception {
    String[] args = {"-f", wrongParcelFile};
    app.run(args);
    assertEquals("", err.toString());
    assertOccurences(out.toString(), "==>", 3);
    assertOccurences(out.toString(), "===>", 1);
    assertTrue(out.toString().contains("unexpected top level"));
    assertTrue(out.toString().contains("No parcel.json file"));
  }

  @Test
  public void testBadParcelFilename() throws Exception {
    String[] args = {"-f", goodParcel};
    app.run(args);
    assertEquals("", err.toString());
    assertTrue(out.toString().contains("not a valid parcel filename"));
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

  private void assertOccurences(String haystack, String needle, int expected) {
    int index = -1;
    int count = 0;
    while ((index = haystack.indexOf(needle)) > -1) {
      haystack = haystack.substring(index + 1);
      count++;
    };
    assertEquals(expected, count);
  }
}
