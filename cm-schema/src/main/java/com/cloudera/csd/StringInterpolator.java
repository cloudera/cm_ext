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
package com.cloudera.csd;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * Provides string interpolation on a string
 * that has substitution variables. For example:
 * "My name is ${name}" when provided:
 * name -> "Bob" then this string gets interpolated
 * to: "My name is Bob"
 */
public class StringInterpolator {

  private static final String START_TOKEN = "\\$\\{";
  private static final String END_TOKEN = "\\}";

  private static final String REGEX = START_TOKEN + "([^}]+)" + END_TOKEN;
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  /**
   * An interface that facilitates getting
   * variable information from the caller.
   */
  public interface VariableProvider {
    @Nullable
    String provide(String variableName);

  }

  /**
   * A convenience method for
   * {@link #interpolate(String,
   * StringInterpolator.VariableProvider)}.
   * Calls toString() on the variable values.
   */
  public String interpolate(String template,
                            final Map<String, ?> variables) {
    return interpolate(template, new VariableProvider() {
      public String provide(String variableName) {
        Object value = variables.get(variableName);
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    });
  }

  /**
   * Performs the search and replace on the template given the
   * substitution variables. If there is a variable in
   * the message but there is no corresponding variable
   * in the map, an IllegalArgumentException is thrown.
   *
   * @param template the template.
   * @param provider the variable provider
   * @return the converted template.
   */
  public String interpolate(String template,
                            VariableProvider provider) {
    Matcher m = PATTERN.matcher(template);
    String result = template;
    while (m.find()) {
      String var = m.group(); // This will be: ${myVar}
      String varName = m.group(1); // This will be: myVar
      String value = provider.provide(varName);
      if (value == null) {
        String msg = String.format(
            "The variable [%s] does not have a corresponding value.", var);
        throw new IllegalArgumentException(msg);
      }
      result = result.replaceFirst(REGEX, value);
    }
    return result;
  }

  /**
   * Return all the placeholder variables that exist in
   * the template.
   *
   * @param template the template.
   * @return the set of variables in the template.
   */
  public Set<String> getVariables(String template) {
    Set<String> variables = Sets.newHashSet();
    Matcher m = PATTERN.matcher(template);
    while (m.find()) {
      variables.add(m.group(1));
    }
    return ImmutableSet.copyOf(variables);
  }

  /**
   * Iterates through all the values of the map and substitutes
   * variables as given by the provider. If the templates
   * map is null, an empty map is returned.
   *
   * @param templates the templates map.
   * @param provider the variable provider.
   * @return a new map with converted values.
   */
  public Map<String, String> interpolateValues(@Nullable Map<String, String> templates,
                                               VariableProvider provider) {
    if (templates == null) {
      return ImmutableMap.of();
    }
    Function<String, String> transformer = transformer(provider);
    return Maps.transformValues(templates, transformer);
  }

  /**
   * Iterates through all the values in the list and substitutes
   * variables as given by the provider. If the list is null,
   * an empty list is returned.
   *
   * @param templates the list of templates.
   * @param provider the variable provider.
   * @return a new list with converted values.
   */
  public List<String> interpolateList(@Nullable List<String> templates,
                                      VariableProvider provider) {
    if (templates == null) {
      return ImmutableList.of();
    }
    Function<String, String> transformer = transformer(provider);
    return Lists.transform(templates, transformer);
  }

  /**
   * The function used to iterate through the collections.
   */
  private Function<String, String> transformer(final VariableProvider provider) {
    return new Function<String, String>() {
      public String apply(@Nullable String input) {
        Preconditions.checkNotNull(input);
        return interpolate(input, provider);
      }
    };
  }
}
