/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Class containing the constraints for filtering nested properties.
 *
 * @author Vaadin Ltd
 * @since 8.2
 */
public class PropertyFilterDefinition implements Serializable {
    private int maxNestingDepth;
    private List<String> ignorePackageNamesStartingWith;

    /**
     * Create a property filter with max nesting depth and package names to
     * ignore.
     *
     * @param maxNestingDepth
     *            The maximum amount of nesting levels for sub-properties.
     * @param ignorePackageNamesStartingWith
     *            Ignore package names that start with this string, for example
     *            "java.lang".
     */
    public PropertyFilterDefinition(int maxNestingDepth,
            List<String> ignorePackageNamesStartingWith) {
        this.maxNestingDepth = maxNestingDepth;
        this.ignorePackageNamesStartingWith = ignorePackageNamesStartingWith;
    }

    /**
     * Returns the maximum amount of nesting levels for sub-properties.
     *
     * @return maximum nesting depth
     */
    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    /**
     * Returns a list of package name prefixes to ignore.
     *
     * @return list of strings that
     */
    public List<String> getIgnorePackageNamesStartingWith() {
        return ignorePackageNamesStartingWith;
    }

    /**
     * Get the default nested property filtering conditions.
     *
     * @return default property filter
     */
    public static PropertyFilterDefinition getDefaultFilter() {
        return new PropertyFilterDefinition(
                BeanPropertySet.NestedBeanPropertyDefinition.MAX_PROPERTY_NESTING_DEPTH,
                Arrays.asList("java"));
    }
}
