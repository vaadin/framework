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
package com.vaadin.client.ui;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.vaadin.shared.ApplicationConstants;

/**
 * A font-based icon implementation.
 * <p>
 * The icon represents a specific character (identified by codepoint,
 * {@link #getCodepoint()}, {@link #setCodepoint(int)}) within a specific font
 * (identified by font-family, {@link #getFontFamily()},
 * {@link #setFontFamily(String)}).
 * </p>
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class FontIcon extends Icon {

    private int codepoint;
    private String fontFamily;

    public FontIcon() {
        setElement(DOM.createSpan());
        setStyleName(CLASSNAME);
    }

    @Override
    public void setUri(String uri) {
        String[] parts = uri.substring(
                ApplicationConstants.FONTICON_PROTOCOL_PREFIX.length()).split(
                "/");
        setFontFamily(URL.decode(parts[0]));
        setCodepoint(Integer.parseInt(parts[1], 16));
    }

    /**
     * Not implemeted for {@link FontIcon} yet.
     * 
     * @see com.vaadin.client.ui.Icon#setAlternateText(java.lang.String)
     */
    @Override
    public void setAlternateText(String alternateText) {
        // TODO this is mostly for WAI-ARIA and should be implemented in an
        // appropriate way.

    }

    /**
     * Sets the font-family from which this icon comes. Use
     * {@link #setCodepoint(int)} to specify a particular icon (character)
     * within the font.
     * 
     * @param fontFamily
     *            font-family name
     */
    protected void setFontFamily(String fontFamily) {
        if (this.fontFamily != null) {
            removeStyleName(getFontStylename());
        }
        this.fontFamily = fontFamily;
        if (fontFamily != null) {
            addStyleName(getFontStylename());
        }
    }

    /**
     * Gets the font-family from which this icon comes. Use
     * {@link #getCodepoint()} to find out which particular icon (character)
     * within the font this is.
     * 
     * @return font-family name
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets the codepoint indicating which particular icon (character) within
     * the font-family this is.
     * 
     * @param codepoint
     */
    protected void setCodepoint(int codepoint) {
        this.codepoint = codepoint;
        getElement().setInnerText(new String(Character.toChars(codepoint)));
    }

    /**
     * Gets the codepoint indicating which particular icon (character) within
     * the font-family this is.
     * 
     * @return
     */
    public int getCodepoint() {
        return codepoint;
    }

    /**
     * Get the font-family based stylename used to apply the font-family.
     * 
     * @since 7.2
     * @return stylename used to apply font-family
     */
    protected String getFontStylename() {
        if (fontFamily == null) {
            return null;
        }
        return fontFamily.replace(' ', '-');
    }

    /**
     * Checks whether or not the given uri is a font icon uri. Does not check
     * whether or not the font icon is available and can be rendered.
     * 
     * @since 7.2
     * @param uri
     * @return true if it's a fonticon uri
     */
    public static boolean isFontIconUri(String uri) {
        return uri != null
                && uri.startsWith(ApplicationConstants.FONTICON_PROTOCOL_PREFIX);
    }

    @Override
    public String getUri() {
        if (fontFamily == null) {
            return null;
        }
        return ApplicationConstants.FONTICON_PROTOCOL_PREFIX + fontFamily + "/"
                + codepoint;
    }
}
