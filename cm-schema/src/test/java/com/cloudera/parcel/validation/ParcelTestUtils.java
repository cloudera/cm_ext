// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cloudera.parcel.validation;

import com.cloudera.common.Parser;
import com.cloudera.parcel.components.JsonAlternativesParser;
import com.cloudera.parcel.components.JsonManifestParser;
import com.cloudera.parcel.components.JsonParcelParser;
import com.cloudera.parcel.components.JsonPermissionsParser;
import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.cloudera.parcel.descriptors.ManifestDescriptor;
import com.cloudera.parcel.descriptors.ParcelDescriptor;
import com.cloudera.parcel.descriptors.PermissionsDescriptor;
import com.cloudera.validation.DescriptorValidator;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class ParcelTestUtils {

  private static final JsonParcelParser PARCEL_PARSER = new JsonParcelParser();
  private static final JsonAlternativesParser ALTERNATIVES_PARSER = new JsonAlternativesParser();
  private static final JsonPermissionsParser PERMISSIONS_PARSER = new JsonPermissionsParser();
  private static final JsonManifestParser MANIFEST_PARSER = new JsonManifestParser();

  public static final ParcelDescriptor FULL_DESCRIPTOR = getParserJson("good_parcel.json");
  public static final DescriptorValidator<ParcelDescriptor> FAKE_VALIDATOR = getAlwaysPassingValidator();

  private static final String RESOURCE_PATH = "/com/cloudera/parcel/";
  public static final String PARCEL_VALIDATOR_RESOURCE_PATH = RESOURCE_PATH + "validator/";
  public static final String PARCEL_PARSER_RESOURCE_PATH = RESOURCE_PATH + "parser/";

  private static <T> T parseJson(Parser<T> parser, String path) {
    try {
      InputStream stream = ParcelTestUtils.class.getResourceAsStream(path);
      return parser.parse(IOUtils.toByteArray(stream));
    } catch (IOException io) {
      throw new RuntimeException(io);
    }
  }

  public static ParcelDescriptor getParserJson(String filename) {
    return parseJson(PARCEL_PARSER, PARCEL_PARSER_RESOURCE_PATH + filename);
  }

  public static ParcelDescriptor getValidatorJson(String filename) {
    return parseJson(PARCEL_PARSER, PARCEL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static AlternativesDescriptor getParserAlternativesJson(String filename) {
    return parseJson(ALTERNATIVES_PARSER, PARCEL_PARSER_RESOURCE_PATH + filename);
  }

  public static AlternativesDescriptor getValidatorAlternativesJson(String filename) {
    return parseJson(ALTERNATIVES_PARSER, PARCEL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static PermissionsDescriptor getParserPermissionsJson(String filename) {
    return parseJson(PERMISSIONS_PARSER, PARCEL_PARSER_RESOURCE_PATH + filename);
  }

  public static PermissionsDescriptor getValidatorPermissionsJson(String filename) {
    return parseJson(PERMISSIONS_PARSER, PARCEL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static ManifestDescriptor getParserManifestJson(String filename) {
    return parseJson(MANIFEST_PARSER, PARCEL_PARSER_RESOURCE_PATH + filename);
  }

  public static ManifestDescriptor getValidatorManifestJson(String filename) {
    return parseJson(MANIFEST_PARSER, PARCEL_VALIDATOR_RESOURCE_PATH + filename);
  }

  public static <T> DescriptorValidator<T> getAlwaysPassingValidator() {
    return new DescriptorValidator<T>() {
      public Set<String> validate(T serviceDescriptor) {
        return Sets.newHashSet();
      }
    };
  }

  public static byte[] getParcelJson(String name) throws IOException {

    InputStream stream = null;
    try {
      stream = ParcelTestUtils.class
          .getResourceAsStream("/com/cloudera/parcel/parser/" + name);
      return IOUtils.toByteArray(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
}
