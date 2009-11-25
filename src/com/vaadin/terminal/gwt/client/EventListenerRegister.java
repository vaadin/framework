/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * EventListenerRegister is used internally for keeping track of which component
 * events have registered listeners on the server-side.
 * 
 * @author davengo GmbH (Germany/Berlin, www.davengo.com), IT Mill
 * @since 6.2
 * 
 */
public class EventListenerRegister {

    public static final String REGISTERED_EVENT_LISTENERS_ATTRIBUTE = "eventListeners";

    private List<String> eventRegistrations;

    EventListenerRegister() {
    }

    /**
     * Checks if there is a registered server side listener for the event.
     * 
     * @param eventIdentifier
     *            The identifier for the event
     * @return true if at least one listener has been registered on server side
     *         for the event identified by eventIdentifier.
     */
    boolean hasEventListeners(String eventIdentifier) {
        return ((!(eventRegistrations == null)) && eventRegistrations
                .contains(eventIdentifier));
    }

    /**
     * Stores the event listeners registered on server-side and passed along in
     * the UIDL.
     * 
     * @param componentUIDL
     *            The UIDL for the component
     * @since 6.2
     */
    void registerEventListenersFromUIDL(UIDL componentUIDL) {

        // read out the request event handlers
        if (componentUIDL.hasAttribute(REGISTERED_EVENT_LISTENERS_ATTRIBUTE)) {
            String[] registeredListeners = componentUIDL
                    .getStringArrayAttribute(REGISTERED_EVENT_LISTENERS_ATTRIBUTE);

            if (registeredListeners == null || registeredListeners.length == 0) {
                eventRegistrations = null;
            } else {
                eventRegistrations = new ArrayList<String>(
                        registeredListeners.length);
                for (String listener : registeredListeners) {
                    eventRegistrations.add(listener);
                }
            }

        }

    }

}
