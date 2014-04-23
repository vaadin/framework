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

/**
 * Common String manipulator utilities used in VaadinFinderLocatorStrategy and
 * SelectorPredicates.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class LocatorUtil {

    /**
     * Find first occurrence of character that's not inside quotes starting from
     * specified index.
     * 
     * @param str
     *            Full string for searching
     * @param find
     *            Character we want to find
     * @param startingAt
     *            Index where we start
     * @return Index of character. -1 if character not found
     */
    protected static int indexOfIgnoringQuoted(String str, char find,
            int startingAt) {
        boolean quote = false;
        String quoteChars = "'\"";
        char currentQuote = '"';
        for (int i = startingAt; i < str.length(); ++i) {
            char cur = str.charAt(i);
            if (quote) {
                if (cur == currentQuote) {
                    quote = !quote;
                }
                continue;
            } else if (cur == find) {
                return i;
            } else {
                if (quoteChars.indexOf(cur) >= 0) {
                    currentQuote = cur;
                    quote = !quote;
                }
            }
        }
        return -1;
    }

    /**
     * Find first occurrence of character that's not inside quotes starting from
     * the beginning of string.
     * 
     * @param str
     *            Full string for searching
     * @param find
     *            Character we want to find
     * @return Index of character. -1 if character not found
     */
    protected static int indexOfIgnoringQuoted(String str, char find) {
        return indexOfIgnoringQuoted(str, find, 0);
    }
}
