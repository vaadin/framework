/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

import com.vaadin.Application;

/**
 * Event used by
 * {@link ApplicationStartedListener#applicationStarted(ApplicationStartedEvent)}
 * .
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class ApplicationStartedEvent extends EventObject {
    private final Application application;

    /**
     * Creates a new event.
     * 
     * @param context
     *            the add-on context that will fire the event
     * @param application
     *            the application that has been started
     */
    public ApplicationStartedEvent(AddonContext context, Application application) {
        super(context);
        this.application = application;
    }

    /**
     * Gets the add-on context from which this event originated.
     * 
     * @return the add-on context that fired the
     */
    public AddonContext getContext() {
        return (AddonContext) getSource();
    }

    /**
     * Gets the newly started Application.
     * 
     * @return the newly created application
     */
    public Application getApplication() {
        return application;
    }

}
