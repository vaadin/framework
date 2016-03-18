/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.componentlocator;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectorPredicates are statements about the state of different components
 * that VaadinFinderLocatorStrategy is finding. SelectorPredicates also provide
 * useful information of said components to debug window by giving means to
 * provide better variable naming.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class SelectorPredicate {
    private String name = "";
    private String value = "";
    private boolean wildcard = false;
    private int index = -1;

    public static List<SelectorPredicate> extractPostFilterPredicates(
            String path) {
        if (path.startsWith("(")) {
            return extractPredicates(path.substring(path.lastIndexOf(')')));
        }
        return new ArrayList<SelectorPredicate>();
    }

    /**
     * Generate a list of predicates from a single predicate string
     * 
     * @param str
     *            a comma separated string of predicates
     * @return a List of Predicate objects
     */
    public static List<SelectorPredicate> extractPredicates(String path) {
        List<SelectorPredicate> predicates = new ArrayList<SelectorPredicate>();

        String predicateStr = extractPredicateString(path);
        if (null == predicateStr || predicateStr.length() == 0) {
            return predicates;
        }

        // Extract input strings
        List<String> input = readPredicatesFromString(predicateStr);

        // Process each predicate into proper predicate descriptor
        for (String s : input) {
            SelectorPredicate p = new SelectorPredicate();
            s = s.trim();

            try {
                // If we can parse out the predicate as a pure index argument,
                // stop processing here.
                p.index = Integer.parseInt(s);
                predicates.add(p);

                continue;
            } catch (Exception e) {
                p.index = -1;
            }

            int idx = LocatorUtil.indexOfIgnoringQuoted(s, '=');
            if (idx < 0) {
                continue;
            }
            p.name = s.substring(0, idx);
            p.value = s.substring(idx + 1);

            if (p.value.equals("?")) {
                p.wildcard = true;
                p.value = null;
            } else {
                // Only unquote predicate value once we're sure it's a proper
                // value...

                p.value = unquote(p.value);
            }

            predicates.add(p);
        }
        // Move any (and all) index predicates to last place in the list.
        for (int i = 0, l = predicates.size(); i < l - 1; ++i) {
            if (predicates.get(i).index > -1) {
                predicates.add(predicates.remove(i));
                --i;
                --l;
            }
        }

        return predicates;
    }

    /**
     * Splits the predicate string to list of predicate strings.
     * 
     * @param predicateStr
     *            Comma separated predicate strings
     * @return List of predicate strings
     */
    private static List<String> readPredicatesFromString(String predicateStr) {
        List<String> predicates = new ArrayList<String>();
        int prevIdx = 0;
        int idx = LocatorUtil.indexOfIgnoringQuoted(predicateStr, ',', prevIdx);

        while (idx > -1) {
            predicates.add(predicateStr.substring(prevIdx, idx));
            prevIdx = idx + 1;
            idx = LocatorUtil.indexOfIgnoringQuoted(predicateStr, ',', prevIdx);
        }
        predicates.add(predicateStr.substring(prevIdx));

        return predicates;
    }

    /**
     * Returns the predicate string, i.e. the string between the brackets in a
     * path fragment. Examples: <code>
     * VTextField[0] => 0
     * VTextField[caption='foo'] => caption='foo'
     * </code>
     * 
     * @param pathFragment
     *            The path fragment from which to extract the predicate string.
     * @return The predicate string for the path fragment or empty string if not
     *         found.
     */
    private static String extractPredicateString(String pathFragment) {
        int ixOpenBracket = LocatorUtil
                .indexOfIgnoringQuoted(pathFragment, '[');
        if (ixOpenBracket >= 0) {
            int ixCloseBracket = LocatorUtil.indexOfIgnoringQuoted(
                    pathFragment, ']', ixOpenBracket);
            return pathFragment.substring(ixOpenBracket + 1, ixCloseBracket);
        }
        return "";
    }

    /**
     * Removes the surrounding quotes from a string if it is quoted.
     * 
     * @param str
     *            the possibly quoted string
     * @return an unquoted version of str
     */
    private static String unquote(String str) {
        if ((str.startsWith("\"") && str.endsWith("\""))
                || (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the wildcard
     */
    public boolean isWildcard() {
        return wildcard;
    }

    /**
     * @param wildcard
     *            the wildcard to set
     */
    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }
}
