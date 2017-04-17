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
package com.cloudera.validation;

import com.cloudera.common.Parser;
import com.cloudera.csd.validation.constraints.DeprecationChecks;
import com.cloudera.csd.validation.constraints.ServiceDependencyValidationGroup;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

/**
 * This class runs validations on entities using a matched {@link #Parser} and
 * {@link #DescriptorValidator}.
 */
public class DescriptorRunner<T> implements ValidationRunner {

  private Parser<T> parser;
  private DescriptorValidator<T> validator;

  public DescriptorRunner(Parser<T> parser,
                          DescriptorValidator<T> validator) {
    this.parser = parser;
    this.validator = validator;
  }

  @Override
  public boolean run(String target, Writer writer)
        throws IOException {

    FileInputStream stream = null;
    try {
      stream = new FileInputStream(target);
      return run(target, IOUtils.toByteArray(stream), writer);
    } catch (Exception e) {
      writer.write(String.format("==> %s\n", e.getMessage()));
      return false;
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  /**
   * Run the validation against a byte array.
   *
   * @param name The name of the target that was loaded into the byte array.
   * @param data The byte array
   * @param writer to write validation errors to
   * @return true if validation passed, false otherwise
   * @throws IOException if we can't write to the outputStream
   */
  public boolean run(String name, byte[] data, Writer writer)
        throws IOException {
    try {
      T descriptor = parser.parse(data);
      Set<String> errors = validator.validate(descriptor);
      Set<String> dependencyErrors = validator.validate(descriptor, ServiceDependencyValidationGroup.class);
      Set<String> deprecationErrors = validator.validate(descriptor, DeprecationChecks.class);

      writer.write("Validating: " + name + "\n");
      for (String error : errors) {
        writer.write(String.format("==> %s\n", error));
      }

      if (!dependencyErrors.isEmpty()) {
        writer.write("Invalid service dependencies:\n");
        for (String error : dependencyErrors) {
          writer.write(String.format("==> %s\n", error));
        }
      }

      if (!deprecationErrors.isEmpty()) {
        writer.write("Deprecated (might fail in future versions):\n");
        for (String error : deprecationErrors) {
          writer.write(String.format("==> %s\n", error));
        }
      }
      return errors.isEmpty() && dependencyErrors.isEmpty();
    } catch (UnrecognizedPropertyException e) {
      List<String> elements = Lists.newArrayList();
      for (Reference r : e.getPath()) {
        elements.add(r.getFieldName());
      }
      writer.write(String.format(
          "==> Unrecognized field \"%s\". Recognized fields are \"%s\"\n",
          Joiner.on('.').join(elements),
          e.getKnownPropertyIds().toString()));
      return false;
    } catch (Exception e) {
      writer.write(String.format("==> %s\n", e.getMessage()));
      return false;
    }
  }
}
