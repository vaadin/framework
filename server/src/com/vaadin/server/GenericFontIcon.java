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
package com.vaadin.server;

/**
 * A generic implementation of {@link FontIcon} interface
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class GenericFontIcon implements FontIcon {
    private final String fontFamily;
    private final int codePoint;

    /**
     * Creates a new instance of GenericFontIcon with given font family and
     * codepoint
     * 
     * @param fontFamily
     *            Name of the type face that is used to display icons
     * @param codepoint
     *            Numerical code point in the font
     */
    public GenericFontIcon(String fontFamily, int codepoint) {
        this.fontFamily = fontFamily;
        codePoint = codepoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.FontIcon#getFontFamily()
     */
    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
    public String getMIMEType() {
        throw new UnsupportedOperationException(FontIcon.class.getSimpleName()
                + " should not be used where a MIME type is needed.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.FontIcon#getCodepoint()
     */
    @Override
    public int getCodepoint() {
        return codePoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.FontIcon#getHtml()
     */
    @Override
    public String getHtml() {
        return getHtml(fontFamily, codePoint);
    }

    /**
     * Utility method for generating HTML that displays an icon from specific
     * fontFamiliy with a given codePoint in the font
     * 
     * @param fontFamily
     *            Name of the font family
     * @param codePoint
     *            Icon's character code point in the font
     * @return
     */
    public static String getHtml(String fontFamily, int codePoint) {
        return "<span class=\"v-icon\" style=\"font-family: " + fontFamily
                + ";\">&#x" + Integer.toHexString(codePoint) + ";</span>";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + codePoint;
        result = prime * result
                + ((fontFamily == null) ? 0 : fontFamily.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GenericFontIcon)) {
            return false;
        }
        GenericFontIcon other = (GenericFontIcon) obj;
        if (codePoint != other.codePoint) {
            return false;
        }
        if (fontFamily == null) {
            if (other.fontFamily != null) {
                return false;
            }
        } else if (!fontFamily.equals(other.fontFamily)) {
            return false;
        }
        return true;
    }
}
