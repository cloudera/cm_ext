// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
package com.cloudera.cli.validator.components;

import com.cloudera.cli.validator.ValidationRunner;
import com.cloudera.common.Parser;
import com.cloudera.validation.DescriptorValidator;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

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
      writer.write("Validating: " + name + "\n");
      T descriptor = parser.parse(data);
      Set<String> errors = validator.validate(descriptor);
      for (String error : errors) {
        writer.write(String.format("==> %s\n", error));
      }
      return errors.isEmpty();
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
