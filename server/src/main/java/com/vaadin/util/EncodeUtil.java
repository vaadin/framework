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
package com.vaadin.util;

import java.nio.charset.Charset;

/**
 * Utilities related to various encoding schemes.
 *
 * @author Vaadin Ltd
 * @since 7.7.7
 */
public class EncodeUtil {
    private static final Charset utf8 = Charset.forName("UTF-8");

    private EncodeUtil() {
        // Static utils only
    }

    /**
     * Encodes the given string to UTF-8 <code>value-chars</code> as defined in
     * RFC5987 for use in e.g. the <code>Content-Disposition</code> HTTP header.
     *
     * @param value
     *            the string to encode, not <code>null</code>
     * @return the encoded string
     */
    public static String rfc5987Encode(String value) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.length();) {
            int cp = value.codePointAt(i);
            if (cp < 127 && (Character.isLetterOrDigit(cp) || cp == '.')) {
                builder.append((char) cp);
            } else {
                // Create string from a single code point
                String cpAsString = new String(new int[] { cp }, 0, 1);

                appendHexBytes(builder, cpAsString.getBytes(utf8));
            }

            // Advance to the next code point
            i += Character.charCount(cp);
        }

        return builder.toString();
    }

    private static void appendHexBytes(StringBuilder builder, byte[] bytes) {
        for (byte byteValue : bytes) {
            // mask with 0xFF to compensate for "negative" values
            int intValue = byteValue & 0xFF;
            String hexCode = Integer.toString(intValue, 16);
            builder.append('%').append(hexCode);
        }
    }

}
