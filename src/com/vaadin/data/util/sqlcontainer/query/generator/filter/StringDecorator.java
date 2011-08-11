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
public class StringDecorator implements Serializable{

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
