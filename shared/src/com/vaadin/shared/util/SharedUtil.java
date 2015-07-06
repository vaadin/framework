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
package com.vaadin.shared.util;

import java.io.Serializable;
import java.util.Locale;

/**
 * Misc internal utility methods used by both the server and the client package.
 *
 * @author Vaadin Ltd
 * @since 7.1
 *
 */
public class SharedUtil implements Serializable {
    /**
     * Checks if a and b are equals using {@link #equals(Object)}. Handles null
     * values as well. Does not ensure that objects are of the same type.
     * Assumes that the first object's equals method handle equals properly.
     *
     * @param o1
     *            The first value to compare
     * @param o2
     *            The second value to compare
     * @return true if the objects are equal, false otherwise
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }

        return o1.equals(o2);
    }

    /**
     * Trims trailing slashes (if any) from a string.
     *
     * @param value
     *            The string value to be trimmed. Cannot be null.
     * @return String value without trailing slashes.
     */
    public static String trimTrailingSlashes(String value) {
        return value.replaceAll("/*$", "");
    }

    /**
     * RegEx pattern to extract the width/height values.
     */
    public static final String SIZE_PATTERN = "^(-?\\d*(?:\\.\\d+)?)(%|px|em|rem|ex|in|cm|mm|pt|pc)?$";

    /**
     * Splits a camelCaseString into an array of words with the casing
     * preserved.
     * 
     * @since 7.4
     * @param camelCaseString
     *            The input string in camelCase format
     * @return An array with one entry per word in the input string
     */
    public static String[] splitCamelCase(String camelCaseString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (Character.isUpperCase(c) && isWordComplete(camelCaseString, i)) {
                sb.append(' ');
            }
            sb.append(c);
        }
        return sb.toString().split(" ");
    }

    private static boolean isWordComplete(String camelCaseString, int i) {
        if (i == 0) {
            // Word can't end at the beginning
            return false;
        } else if (!Character.isUpperCase(camelCaseString.charAt(i - 1))) {
            // Word ends if previous char wasn't upper case
            return true;
        } else if (i + 1 < camelCaseString.length()
                && !Character.isUpperCase(camelCaseString.charAt(i + 1))) {
            // Word ends if next char isn't upper case
            return true;
        } else {
            return false;
        }
    }

    /**
     * Converts a camelCaseString to a human friendly format (Camel case
     * string).
     * <p>
     * In general splits words when the casing changes but also handles special
     * cases such as consecutive upper case characters. Examples:
     * <p>
     * {@literal MyBeanContainer} becomes {@literal My Bean Container}
     * {@literal AwesomeURLFactory} becomes {@literal Awesome URL Factory}
     * {@literal SomeUriAction} becomes {@literal Some Uri Action}
     * 
     * @since 7.4
     * @param camelCaseString
     *            The input string in camelCase format
     * @return A human friendly version of the input
     */
    public static String camelCaseToHumanFriendly(String camelCaseString) {
        String[] parts = splitCamelCase(camelCaseString);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = capitalize(parts[i]);
        }
        return join(parts, " ");
    }

    private static boolean isAllUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!Character.isUpperCase(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Joins the words in the input array together into a single string by
     * inserting the separator string between each word.
     * 
     * @since 7.4
     * @param parts
     *            The array of words
     * @param separator
     *            The separator string to use between words
     * @return The constructed string of words and separators
     */
    public static String join(String[] parts, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            sb.append(separator);
        }
        return sb.substring(0, sb.length() - separator.length());
    }

    /**
     * Capitalizes the first character in the given string in a way suitable for
     * use in code (methods, properties etc)
     * 
     * @since 7.4
     * @param string
     *            The string to capitalize
     * @return The capitalized string
     */
    public static String capitalize(String string) {
        if (string == null) {
            return null;
        }

        if (string.length() <= 1) {
            return string.toUpperCase();
        }

        return string.substring(0, 1).toUpperCase(Locale.ENGLISH)
                + string.substring(1);
    }

    /**
     * Converts a property id to a human friendly format. Handles nested
     * properties by only considering the last part, e.g. "address.streetName"
     * is equal to "streetName" for this method.
     * 
     * @since 7.4
     * @param propertyId
     *            The propertyId to format
     * @return A human friendly version of the property id
     */
    public static String propertyIdToHumanFriendly(Object propertyId) {
        String string = propertyId.toString();
        if (string.isEmpty()) {
            return "";
        }

        // For nested properties, only use the last part
        int dotLocation = string.lastIndexOf('.');
        if (dotLocation > 0 && dotLocation < string.length() - 1) {
            string = string.substring(dotLocation + 1);
        }

        return camelCaseToHumanFriendly(string);
    }

    /**
     * Adds the get parameters to the uri and returns the new uri that contains
     * the parameters.
     *
     * @param uri
     *            The uri to which the parameters should be added.
     * @param extraParams
     *            One or more parameters in the format "a=b" or "c=d&e=f". An
     *            empty string is allowed but will not modify the url.
     * @return The modified URI with the get parameters in extraParams added.
     */
    public static String addGetParameters(String uri, String extraParams) {
        if (extraParams == null || extraParams.length() == 0) {
            return uri;
        }
        // RFC 3986: The query component is indicated by the first question
        // mark ("?") character and terminated by a number sign ("#") character
        // or by the end of the URI.
        String fragment = null;
        int hashPosition = uri.indexOf('#');
        if (hashPosition != -1) {
            // Fragment including "#"
            fragment = uri.substring(hashPosition);
            // The full uri before the fragment
            uri = uri.substring(0, hashPosition);
        }

        if (uri.contains("?")) {
            uri += "&";
        } else {
            uri += "?";
        }
        uri += extraParams;

        if (fragment != null) {
            uri += fragment;
        }

        return uri;
    }

    /**
     * Converts a dash ("-") separated string into camelCase.
     * <p>
     * Examples:
     * <p>
     * {@literal foo} becomes {@literal foo} {@literal foo-bar} becomes
     * {@literal fooBar} {@literal foo--bar} becomes {@literal fooBar}
     * 
     * @since 7.5
     * @param dashSeparated
     *            The dash separated string to convert
     * @return a camelCase version of the input string
     */
    public static String dashSeparatedToCamelCase(String dashSeparated) {
        if (dashSeparated == null) {
            return null;
        }
        String[] parts = dashSeparated.split("-");
        for (int i = 1; i < parts.length; i++) {
            parts[i] = capitalize(parts[i]);
        }

        return join(parts, "");
    }

}
