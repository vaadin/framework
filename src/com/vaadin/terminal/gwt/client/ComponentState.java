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
    // TODO more javadoc

    // TODO constants for the state attributes for now
    public static final String STATE_HEIGHT = "height";
    public static final String STATE_WIDTH = "width";
    public static final String STATE_STYLE = "style";
    public static final String STATE_READONLY = "readonly";
    public static final String STATE_IMMEDIATE = "immediate";
    public static final String STATE_DISABLED = "disabled";
    public static final String STATE_CAPTION = "caption";
    public static final String STATE_DESCRIPTION = "description";

}
