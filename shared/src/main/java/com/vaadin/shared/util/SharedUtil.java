/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
            if (Character.isUpperCase(c)
                    && isWordComplete(camelCaseString, i)) {
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
        }
        // Word ends if next char isn't upper case
        return i + 1 < camelCaseString.length()
                && !Character.isUpperCase(camelCaseString.charAt(i + 1));
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

    /**
     * Converts an UPPER_CASE_STRING to a human friendly format (Upper Case
     * String).
     * <p>
     * Splits words on {@code _}. Examples:
     * <p>
     * {@literal MY_BEAN_CONTAINER} becomes {@literal My Bean Container}
     * {@literal AWESOME_URL_FACTORY} becomes {@literal Awesome Url Factory}
     * {@literal SOMETHING} becomes {@literal Something}
     *
     * @since 7.7.4
     * @param upperCaseUnderscoreString
     *            The input string in UPPER_CASE_UNDERSCORE format
     * @return A human friendly version of the input
     */
    public static String upperCaseUnderscoreToHumanFriendly(
            String upperCaseUnderscoreString) {
        String[] parts = upperCaseUnderscoreString.replaceFirst("^_*", "")
                .split("_");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = capitalize(parts[i].toLowerCase(Locale.ROOT));
        }
        return join(parts, " ");
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
        if (parts.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
            sb.append(separator);
        }
        return sb.substring(0, sb.length() - separator.length());
    }

    /**
     * Capitalizes the first character in the given string in a way suitable for
     * use in code (methods, properties etc).
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
            return string.toUpperCase(Locale.ROOT);
        }

        return string.substring(0, 1).toUpperCase(Locale.ROOT)
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

        if (string.matches("^[0-9A-Z_]+$")) {
            // Deal with UPPER_CASE_PROPERTY_IDS
            return upperCaseUnderscoreToHumanFriendly(string);
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
     *            One or more parameters in the format "a=b" or "c=d&amp;e=f".
     *            An empty string is allowed but will not modify the url.
     * @return The modified URI with the get parameters in extraParams added.
     */
    public static String addGetParameters(String uri, String extraParams) {
        if (extraParams == null || extraParams.isEmpty()) {
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

    /**
     * Checks if the given array contains duplicates (according to
     * {@link Object#equals(Object)}.
     *
     * @param values
     *            the array to check for duplicates
     * @return <code>true</code> if the array contains duplicates,
     *         <code>false</code> otherwise
     */
    public static boolean containsDuplicates(Object[] values) {
        int uniqueCount = new HashSet<Object>(Arrays.asList(values)).size();
        return uniqueCount != values.length;
    }

    /**
     * Return duplicate values in the given array in the format
     * "duplicateValue1, duplicateValue2".
     *
     * @param values
     *            the values to check for duplicates
     * @return a comma separated string of duplicates or an empty string if no
     *         duplicates were found
     */
    public static String getDuplicates(Object[] values) {
        HashSet<Object> set = new HashSet<Object>();
        LinkedHashSet<String> duplicates = new LinkedHashSet<String>();
        for (Object o : values) {
            if (!set.add(o)) {
                duplicates.add(String.valueOf(o));
            }

        }
        return join(duplicates.toArray(new String[duplicates.size()]), ", ");
    }

}
