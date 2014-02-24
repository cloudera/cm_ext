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

import com.cloudera.validation.MessageSourceInterpolator;

import java.util.Locale;
import javax.validation.MessageInterpolator;
import javax.validation.MessageInterpolator.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageSourceInterpolatorTest {

  private Locale locale = Locale.getDefault();

  @Mock
  private MessageInterpolator delegate;

  @Mock
  private Context context;

  @InjectMocks
  @Spy
  private MessageSourceInterpolator interpolator;

  @Test
  public void testTranslateStripBrackets() {
    String key = "{javax.validation.constraint.myconstraint.message}";
    String output = interpolator.translate(key);
    assertEquals("javax.validation.constraint.myconstraint.message",
            output);
  }

  @Test
  public void testTranslateStripNoBrackets() {
    String key = "javax.validation.constraint.myconstraint.message";
    String output = interpolator.translate(key);
    assertEquals("javax.validation.constraint.myconstraint.message",
            output);
  }

  @Test
  public void testInterpolate() {
    String key = "javax.validation.constraints.Min.message";
    String eOutput = "must be more than ${value}";

    when(delegate.interpolate(eOutput, context, locale)).thenReturn(eOutput);
    String output = interpolator.interpolate(key, context, locale);
    assertEquals(eOutput, output);
    verify(delegate).interpolate(output, context, locale);
  }

  @Test
  public void testInterpolateBadKey() {
    String key = "someKey";
    when(delegate.interpolate(key, context, locale)).thenReturn(key);
    assertEquals(key, interpolator.interpolate(key, context, locale));
    verify(delegate).interpolate(key, context, locale);
  }
}
