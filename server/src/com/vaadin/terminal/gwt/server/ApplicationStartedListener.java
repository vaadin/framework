/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

import com.vaadin.Application;

/**
 * Listener that gets notified when a new {@link Application} has been started.
 * Add-ons can use this listener to automatically integrate with API tied to the
 * Application API.
 * 
 * @see AddonContext#addApplicationStartedListener(ApplicationStartedListener)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface ApplicationStartedListener extends EventListener {
    /**
     * Tells the listener that an application has been started (meaning that
     * {@link Application#init()} has been invoked.
     * 
     * @param event
     *            details about the event
     */
    public void applicationStarted(ApplicationStartedEvent event);
}
