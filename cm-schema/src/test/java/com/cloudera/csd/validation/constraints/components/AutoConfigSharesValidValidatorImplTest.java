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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.cloudera.csd.descriptors.parameters.MemoryParameter;
import com.cloudera.csd.descriptors.parameters.Parameter;
import com.google.common.collect.ImmutableSet;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AutoConfigSharesValidValidatorImplTest {
  @Mock
  private ConstraintValidatorContext context;

  @Mock
  private MemoryParameter mp1;

  @Mock
  private MemoryParameter mp2;

  @Mock
  private MemoryParameter mp3;

  @Mock
  private MemoryParameter mp4;

  @Mock
  private Parameter<?> p1;

  @Mock
  private Parameter<?> p2;

  @Spy
  @InjectMocks
  private AutoConfigSharesValidValidatorImpl validator;

  @Before
  public void setupMocking() {
    doNothing().when(validator).addViolation(eq(context));
    when(mp1.getAutoConfigShare()).thenReturn(50);
    when(mp2.getAutoConfigShare()).thenReturn(25);
    when(mp3.getAutoConfigShare()).thenReturn(25);
    when(mp4.getAutoConfigShare()).thenReturn(10);
  }

  @Test
  public void testSatisfied() {
    assertTrue(validator.isValid(ImmutableSet.of(mp1, mp2, mp3, p1, p2), context));
  }

  @Test
  public void testTooLittle() {
    assertFalse(validator.isValid(ImmutableSet.of(mp1, mp2, p1, p2), context));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testTooMuch() {
    assertFalse(validator.isValid(ImmutableSet.of(mp1, mp2, mp3, mp4, p1, p2), context));
  }
}
