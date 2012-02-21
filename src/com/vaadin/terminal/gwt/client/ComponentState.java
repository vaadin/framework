/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Default shared state implementation for UI components.
 * 
 * State classes of concrete components should extend this class.
 * 
 * @since 7.0
 */
public class ComponentState extends SharedState {
    private String height = "";
    private String width = "";

    // TODO more javadoc

    public String getHeight() {
        if (height == null) {
            return "";
        }
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean isUndefinedHeight() {
        return "".equals(getHeight());
    }

    public String getWidth() {
        if (width == null) {
            return "";
        }
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isUndefinedWidth() {
        return "".equals(getWidth());
    }

    // TODO constants for the state attributes for now
    public static final String STATE_STYLE = "style";
    public static final String STATE_READONLY = "readonly";
    public static final String STATE_IMMEDIATE = "immediate";
    public static final String STATE_DISABLED = "disabled";
    public static final String STATE_CAPTION = "caption";
    public static final String STATE_DESCRIPTION = "description";

}
