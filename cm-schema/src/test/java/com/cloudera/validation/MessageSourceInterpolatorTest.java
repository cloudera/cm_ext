// Copyright (c) 2013 Cloudera, Inc. All rights reserved.
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
