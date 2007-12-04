/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itmill.toolkit.terminal.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * <p>
 * Implementation of the {@link java.util.regex.Pattern} class with a wrapper
 * aroung the Javascript <a
 * href="http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Guide:Regular_Expressions">RegExp</a>
 * object. As most of the methods delegate to the JavaScript RegExp object,
 * certain differences in the declaration and behaviour of regular expressions
 * must be expected.
 * </p>
 * <p>
 * Please note that neither the {@link java.util.regex.Pattern#compile(String)}
 * method nor {@link java.util.regex.Matcher} instances are supported. For the
 * later, consider using {@link Pattern#match(String)}.
 * </p>
 * 
 * @author George Georgovassilis
 * 
 */
public class Pattern {

    /**
     * Declares that regular expressions should be matched across line borders.
     */
    public final static int MULTILINE = 1;

    /**
     * Declares that characters are matched reglardless of case.
     */
    public final static int CASE_INSENSITIVE = 2;

    private final JavaScriptObject regExp;

    private static JavaScriptObject createExpression(String pattern, int flags) {
        String sFlags = "";
        if ((flags & MULTILINE) != 0) {
            sFlags += "m";
        }
        if ((flags & CASE_INSENSITIVE) != 0) {
            sFlags += "i";
        }
        return _createExpression(pattern, sFlags);
    }

    private static native JavaScriptObject _createExpression(String pattern,
            String flags)/*-{
                                                    		 return new RegExp(pattern, flags);
                                                    		 }-*/;

    private native void _match(String text, List matches)/*-{
                                                    		 var regExp = this.@com.itmill.toolkit.terminal.gwt.client.util.Pattern::regExp;
                                                    		 var result = text.match(regExp);
                                                    		 if (result == null) return;
                                                    		 for (var i=0;i<result.length;i++)
                                                    		 matches.@java.util.ArrayList::add(Ljava/lang/Object;)(result[i]);
                                                    		 }-*/;

    /**
     * Determines wether the specified regular expression is validated by the
     * provided input.
     * 
     * @param regex
     *                Regular expression
     * @param input
     *                String to validate
     * @return <code>true</code> if matched.
     */
    public static boolean matches(String regex, String input) {
        return new Pattern(regex).matches(input);
    }

    /**
     * Escape a provided string so that it will be interpreted as a literal in
     * regular expressions. The current implementation does escape each
     * character even if not neccessary, generating verbose literals.
     * 
     * @param input
     * @return
     */
    public static String quote(String input) {
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            output += "\\" + input.charAt(i);
        }
        return output;
    }

    /**
     * Class constructor
     * 
     * @param pattern
     *                Regular expression
     */
    public Pattern(String pattern) {
        this(pattern, 0);
    }

    /**
     * Class constructor
     * 
     * @param pattern
     *                Regular expression
     * @param flags
     */
    public Pattern(String pattern, int flags) {
        regExp = createExpression(pattern, flags);
    }

    /**
     * This method is borrowed from the JavaScript RegExp object. It parses a
     * string and returns as an array any assignments to parenthesis groups in
     * the pattern's regular expression
     * 
     * @param text
     * @return Array of strings following java's Pattern convention for groups:
     *         Group 0 is the entire input string and the remaining groups are
     *         the matched parenthesis. In case nothing was matched an empty
     *         array is returned.
     */
    public String[] match(String text) {
        final List matches = new ArrayList();
        _match(text, matches);
        final String arr[] = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            arr[i] = matches.get(i).toString();
        }
        return arr;
    }

    /**
     * Determines wether a provided text matches the regular expression
     * 
     * @param text
     * @return
     */
    public native boolean matches(String text)/*-{
                                                    		 var regExp = this.@com.itmill.toolkit.terminal.gwt.client.util.Pattern::regExp;
                                                    		 return regExp.test(text);
                                                    		 }-*/;

    /**
     * Returns the regular expression for this pattern
     * 
     * @return
     */
    public native String pattern()/*-{
                                                    		 var regExp = this.@com.itmill.toolkit.terminal.gwt.client.util.Pattern::regExp;
                                                    		 return regExp.source;
                                                    		 }-*/;

    private native void _split(String input, List results)/*-{
                                                    		 var regExp = this.@com.itmill.toolkit.terminal.gwt.client.util.Pattern::regExp;
                                                    		 var parts = input.split(regExp);
                                                    		 for (var i=0;i<parts.length;i++)
                                                    		 results.@java.util.ArrayList::add(Ljava/lang/Object;)(parts[i]	);
                                                    		 }-*/;

    /**
     * Split an input string by the pattern's regular expression
     * 
     * @param input
     * @return Array of strings
     */
    public String[] split(String input) {
        final List results = new ArrayList();
        _split(input, results);
        final String[] parts = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            parts[i] = (String) results.get(i);
        }
        return parts;
    }

}
