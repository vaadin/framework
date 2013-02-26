/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.sass.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String FOLDER_SEPARATOR = "/"; // folder separator

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\"; // Windows
                                                                 // folder
                                                                 // separator

    private static final String TOP_PATH = ".."; // top folder

    private static final String CURRENT_PATH = "."; // current folder

    public static String cleanPath(String path) {
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR,
                FOLDER_SEPARATOR);
        String[] pathArray = delimitedListToStringArray(pathToUse,
                FOLDER_SEPARATOR);
        List pathElements = new LinkedList();
        int tops = 0;
        for (int i = pathArray.length - 1; i >= 0; i--) {
            if (CURRENT_PATH.equals(pathArray[i])) {
                // do nothing
            } else if (TOP_PATH.equals(pathArray[i])) {
                tops++;
            } else {
                if (tops > 0) {
                    tops--;
                } else {
                    pathElements.add(0, pathArray[i]);
                }
            }
        }
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }
        return collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    public static String replace(String inString, String oldPattern,
            String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();
        // output StringBuffer we'll build up
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));

        // remember to append any characters to the right of a match
        return sbuf.toString();
    }

    public static String[] delimitedListToStringArray(String str,
            String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[] { str };
        }

        List result = new ArrayList();
        int pos = 0;
        int delPos = 0;
        while ((delPos = str.indexOf(delimiter, pos)) != -1) {
            result.add(str.substring(pos, delPos));
            pos = delPos + delimiter.length();
        }
        if (str.length() > 0 && pos <= str.length()) {
            // Add rest of String, but not in case of empty input.
            result.add(str.substring(pos));
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    public static String collectionToDelimitedString(Collection coll,
            String delim, String prefix, String suffix) {
        if (coll == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(prefix).append(it.next()).append(suffix);
            i++;
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection coll,
            String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    /**
     * Check if a String contains a SCSS variable, using whole word match.
     * 
     * @param text
     *            text to be checked
     * @Param varName SCSS variable name to be checked. (Without '$' sign)
     * @return true if the text contains the SCSS variable, false if not
     */
    public static boolean containsVariable(String text, String varName) {
        return containsSubString(text, "$" + varName);
    }

    /**
     * Replace the SCSS variable in a String to its corresponding value, using
     * whole word match.
     * 
     * @param text
     *            text which contains the SCSS variable
     * @param varName
     *            SCSS variable name (Without '$' sign)
     * @param value
     *            the value of the SCSS variable
     * @return the String after replacing
     */
    public static String replaceVariable(String text, String varName,
            String value) {
        return replaceSubString(text, "$" + varName, value);
    }

    /**
     * Check if a String contains a sub string, using whole word match.
     * 
     * @param text
     *            text to be checked
     * @Param sub Sub String to be checked.
     * @return true if the text contains the sub string, false if not
     */
    public static boolean containsSubString(String text, String sub) {
        StringBuilder builder = new StringBuilder();
        // (?![\\w-]) means lookahead, the next one shouldn't be a word
        // character nor a dash.
        builder.append("(?<![\\w-])").append(Pattern.quote(sub))
                .append("(?![\\w-])");
        Pattern pattern = Pattern.compile(builder.toString());
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * Replace the sub string in a String to a value, using whole word match.
     * 
     * @param text
     *            text which contains the sub string
     * @param sub
     *            the sub string
     * @param value
     *            the new value
     * @return the String after replacing
     */
    public static String replaceSubString(String text, String sub, String value) {
        StringBuilder builder = new StringBuilder();
        // (?![\\w-]) means lookahead, the next one shouldn't be a word
        // character nor a dash.
        builder.append("(?<![\\w-])").append(Pattern.quote(sub))
                .append("(?![\\w-])");
        return text.replaceAll(builder.toString(), value);
    }

    /**
     * Remove duplicated sub string in a String given a splitter. Can be used to
     * removed duplicated selectors, e.g., in ".error.error", one duplicated
     * ".error" can be removed.
     * 
     * @param motherString
     *            string which may contains duplicated sub strings
     * @param splitter
     *            the splitter splits the mother string to sub strings
     * @return the mother string with duplicated sub strings removed
     */
    public static String removeDuplicatedSubString(String motherString,
            String splitter) {
        List<String> subStrings = Arrays.asList(motherString.split(Pattern
                .quote(splitter)));
        LinkedHashSet<String> uniqueSubStrings = new LinkedHashSet<String>(
                subStrings);
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String uniqueSubString : uniqueSubStrings) {
            count++;
            builder.append(uniqueSubString);
            if (count < uniqueSubStrings.size()) {
                builder.append(splitter);
            }
        }
        return builder.toString();
    }
}
