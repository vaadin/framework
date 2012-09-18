/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.client.ui;

/**
 * Widgets who wish to be notificed when a shortcut action has been triggered
 * with the widget as a target should implement this interface. The
 * {@link #handleAction(ShortcutAction)} method will be called just before the
 * action is communicated to the server
 * 
 */
public interface ShortcutActionTarget {

    /**
     * Called by the {@link ShortcutActionHandler} just before the shortcut
     * action is sent to the server side
     * 
     * @param action
     *            The action which will be performed on the server side
     * @return Returns true if the shortcut was handled
     */
    boolean handleAction(ShortcutAction action);
}
