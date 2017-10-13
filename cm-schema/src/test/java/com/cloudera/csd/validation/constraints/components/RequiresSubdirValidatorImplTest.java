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
package com.cloudera.csd.validation.constraints.components;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RequiresSubdirValidatorImplTest {

  // readability constants
  private static final boolean VALID = true;
  private static final boolean INVALID = false;

  @Mock
  private ConstraintValidatorContext context;

  @Test
  public void testRequiresSubdir() {
    runValidatorTest("spark-conf/spark-env.sh", VALID);
    runValidatorTest("all-conf/spark/spark-env.sh", VALID);
    runValidatorTest("", VALID);
    runValidatorTest(null, VALID);

    runValidatorTest("spark-env.sh", INVALID);
    runValidatorTest("/spark-env.sh", INVALID);
    runValidatorTest("/conf/spark-env.sh", INVALID);
    runValidatorTest("spark-conf/", INVALID);
  }

  private void runValidatorTest(String filename, boolean expectValid) {
    RequiresSubdirValidatorImpl validator = new RequiresSubdirValidatorImpl();
    boolean valid = validator.isValid(filename, context);
    assertEquals(expectValid, valid);
  }

}