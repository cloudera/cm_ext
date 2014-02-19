// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
