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

}
