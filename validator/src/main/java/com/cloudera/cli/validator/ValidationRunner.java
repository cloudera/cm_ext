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
package com.cloudera.cli.validator;

import java.io.IOException;
import java.io.Writer;

/**
 * Interface for a validation runner.
 *
 * All this interface assumes is that the validation target can be identified by
 * a single string.
 */
public interface ValidationRunner {

  /**
   * Run the validation for the specified target. If there are any violations,
   * print them to the passed in writer.
   *
   * @param target identifier for the validation target
   * @param writer to write validation errors to
   * @return true if validation passed, false otherwise
   * @throws IOException if we can't write to the outputStream
   */
  boolean run(String target, Writer writer) throws IOException;
}
