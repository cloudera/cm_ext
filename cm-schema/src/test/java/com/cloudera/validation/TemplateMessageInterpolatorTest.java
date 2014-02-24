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

import com.cloudera.csd.StringInterpolator;
import com.cloudera.validation.TemplateMessageInterpolator;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.validation.MessageInterpolator;
import javax.validation.MessageInterpolator.Context;
import javax.validation.metadata.ConstraintDescriptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TemplateMessageInterpolatorTest {

  private Map<String, String> objects = ImmutableMap.of("var1", "value1",
      "var2", "value2");

  @Mock
  private MessageInterpolator delegate;

  @Mock
  private Context context;

  private StringInterpolator stringInterpolator = new StringInterpolator();

  private TemplateMessageInterpolator interpolator = new TemplateMessageInterpolator(delegate,
                                                                                     stringInterpolator);

  @Test
  public void testDoInterpolate() {
    String str = "My ${var1} string";
    Map<String, Object> variables = ImmutableMap.<String, Object> of("var1", "value1");
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    when(context.getConstraintDescriptor()).thenReturn(descriptor);
    when(descriptor.getAttributes()).thenReturn(variables);
    assertEquals("My value1 string", interpolator.doInterpolate(str, context));
  }
}
