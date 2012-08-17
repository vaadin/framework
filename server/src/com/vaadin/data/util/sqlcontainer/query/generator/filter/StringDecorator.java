/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import java.io.Serializable;

/**
 * The StringDecorator knows how to produce a quoted string using the specified
 * quote start and quote end characters. It also handles grouping of a string
 * (surrounding it in parenthesis).
 * 
 * Extend this class if you need to support special characters for grouping
 * (parenthesis).
 * 
 * @author Vaadin Ltd
 */
public class StringDecorator implements Serializable {

    private final String quoteStart;
    private final String quoteEnd;

    /**
     * Constructs a StringDecorator that uses the quoteStart and quoteEnd
     * characters to create quoted strings.
     * 
     * @param quoteStart
     *            the character denoting the start of a quote.
     * @param quoteEnd
     *            the character denoting the end of a quote.
     */
    public StringDecorator(String quoteStart, String quoteEnd) {
        this.quoteStart = quoteStart;
        this.quoteEnd = quoteEnd;
    }

    /**
     * Surround a string with quote characters.
     * 
     * @param str
     *            the string to quote
     * @return the quoted string
     */
    public String quote(Object str) {
        return quoteStart + str + quoteEnd;
    }

    /**
     * Groups a string by surrounding it in parenthesis
     * 
     * @param str
     *            the string to group
     * @return the grouped string
     */
    public String group(String str) {
        return "(" + str + ")";
    }
}
