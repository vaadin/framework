package com.vaadin.terminal.gwt.client;

/**
 * GWT's HasFocus is way too overkill for just receiving focus in simple
 * components. Toolkit uses this interface in addition to GWT's HasFocus to pass
 * focus requests from server to actual ui widgets in browsers.
 * 
 * So in to make your server side focusable component receive focus on client
 * side it must either implement this or HasFocus interface.
 */
public interface Focusable {
    /**
     * Sets focus to this widget.
     */
    public void focus();
}
