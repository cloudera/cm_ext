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
package com.cloudera.csd.validation.references.components;

import static org.junit.Assert.*;

import com.cloudera.csd.validation.references.DescriptorPath;
import com.cloudera.csd.validation.references.annotations.Named;
import com.cloudera.csd.validation.references.components.DescriptorVisitorImpl.AbstractNodeProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import javax.validation.Valid;

import org.junit.Test;

public class DescriptorVisitorImplTest {

  private DescriptorVisitorImpl visitor = new DescriptorVisitorImpl();

  @Test
  public void testVisit() {

    NamedSubDescriptor childSub = new NamedSubDescriptor("child");
    SubDescriptor sub = new SubDescriptor("foo", ImmutableSet.of(childSub));

    final Set<String> beforePaths = Sets.newHashSet();
    final Set<String> afterPaths = Sets.newHashSet();

    visitor.visit(sub, new AbstractNodeProcessor<Object>() {
      @Override
      public void beforeNode(Object obj, DescriptorPath path) {
        beforePaths.add(path.toString());
      }

      @Override
      public void afterNode(Object obj, DescriptorPath oldPath) {
        afterPaths.add(oldPath.toString());
      }
    });

    assertEquals(ImmutableSet.of("SubDescriptor",
                                 "SubDescriptor.name",
                                 "SubDescriptor.set",
                                 "SubDescriptor.set.child",
                                 "SubDescriptor.set.child.name"),
                 beforePaths);

    assertEquals(ImmutableSet.of("SubDescriptor",
                                 "SubDescriptor.name",
                                 "SubDescriptor.set",
                                 "SubDescriptor.set.child",
                                 "SubDescriptor.set.child.name"),
                 afterPaths);
  }

  @Test
  public void testVisitNamed() {

    NamedSubDescriptor sub = new NamedSubDescriptor("foo");

    final Set<String> beforePaths = Sets.newHashSet();

    visitor.visit(sub, new AbstractNodeProcessor<Object>() {
      @Override
      public void beforeNode(Object obj, DescriptorPath path) {
        beforePaths.add(path.toString());
      }
    });

    assertEquals(ImmutableSet.of("foo",
                                 "foo.name"),
                 beforePaths);
  }

  @Test
  public void testVisitReturn() {

    String returnStr = visitor.visit(new Descriptor(), new AbstractNodeProcessor<String>() {
      @Override
      public String getResult() {
        return "resultString";
      }
    });
    assertEquals("resultString", returnStr);
  }

  @Test
  public void testNestedObjects() {

    NamedSubDescriptor named1 = new NamedSubDescriptor("named1");
    NamedSubDescriptor named2 = new NamedSubDescriptor("named2");

    SubDescriptor sub1 = new SubDescriptor("sub", ImmutableSet.<NamedSubDescriptor>of());
    SubDescriptor subWithChildren = new SubDescriptor("sub2", ImmutableSet.of(named2));

    Descriptor descriptor = new Descriptor();
    descriptor.sub = subWithChildren;
    descriptor.namedSub = named1;
    descriptor.set = ImmutableSet.of(sub1, subWithChildren);

    final Set<String> beforePaths = Sets.newHashSet();

    visitor.visit(descriptor, new AbstractNodeProcessor<Object>() {
      @Override
      public void beforeNode(Object obj, DescriptorPath path) {
        beforePaths.add(path.toString());
      }
    });

    assertEquals(ImmutableSet.of("Descriptor.sub",
                                 "Descriptor.sub.SubDescriptor",
                                 "Descriptor.sub.SubDescriptor.name",
                                 "Descriptor.sub.SubDescriptor.set",
                                 "Descriptor.sub.SubDescriptor.set.named2",
                                 "Descriptor.sub.SubDescriptor.set.named2.name",

                                 "Descriptor.namedSub",
                                 "Descriptor.namedSub.named1",
                                 "Descriptor.namedSub.named1.name",

                                 "Descriptor.set",

                                 "Descriptor"),
                 beforePaths);

  }

  public static class Descriptor {

    private SubDescriptor sub;
    private NamedSubDescriptor namedSub;
    private Set<SubDescriptor> set;
    private Set<NamedSubDescriptor> namedSet;

    @Valid
    public SubDescriptor getSub() {
      return sub;
    }

    @Valid
    public NamedSubDescriptor getNamedSub() {
      return namedSub;
    }

    public Set<SubDescriptor> getSet() {
      return set;
    }
  }

  @Named
  public static class NamedSubDescriptor {
    private String name;

    public NamedSubDescriptor(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class SubDescriptor {
    private String name;
    private Set<NamedSubDescriptor> set;

    public SubDescriptor(String name, Set<NamedSubDescriptor> set) {
      this.name = name;
      this.set = set;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Valid
    public Set<NamedSubDescriptor> getSet() {
      return set;
    }

    public void setSet(Set<NamedSubDescriptor> set) {
      this.set = set;
    }
  }
}
