// Copyright (c) 2014 Cloudera, Inc. All rights reserved.
package com.cloudera.parcel.components;

import com.cloudera.common.Parser;
import com.cloudera.parcel.descriptors.AlternativeDescriptor;
import com.cloudera.parcel.descriptors.AlternativesDescriptor;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * This class is used to read an alternatives.json file
 */
public class JsonAlternativesParser extends JsonGenericParser<AlternativesDescriptor>
                                    implements Parser<AlternativesDescriptor> {

  /**
   * This adaptor class is required to transform the generic Map, otherwise returned
   * from deserializing an alternatives.json file, into a POJO with the Map stored
   * as a field. This is necessary for JSR303 validation to work. Attempts to
   * validate the Map directly do not validate the values inside it.
   */
  private static class AlternativesDescriptorImpl implements AlternativesDescriptor {
    private final ImmutableMap.Builder<String, AlternativeDescriptor> alternatives = ImmutableMap.builder();

    @Override
    public Map<String, AlternativeDescriptor> getAlternatives() {
      return alternatives.build();
    }

    @JsonAnySetter
    private void add(String name, AlternativeDescriptor alternative) {
      alternatives.put(name, alternative);
    }

  }

  public JsonAlternativesParser() {
    super(new TypeReference<AlternativesDescriptorImpl>() {});
  }
}
