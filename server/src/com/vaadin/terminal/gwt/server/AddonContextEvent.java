/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

/**
 * Event used when an {@link AddonContext} is created and destroyed.
 * 
 * @see AddonContextListener
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class AddonContextEvent extends EventObject {

    /**
     * Creates a new event for the given add-on context.
     * 
     * @param source
     *            the add-on context that created the event
     */
    public AddonContextEvent(AddonContext source) {
        super(source);
    }

    /**
     * Gets the add-on context that created this event.
     * 
     * @return the add-on context that created this event.
     */
    public AddonContext getAddonContext() {
        return (AddonContext) getSource();
    }

}
