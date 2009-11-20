/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * this class is used to store the registered events so a list of the required
 * client event identifiers (that the client-side should listen for and send to
 * the server-side) can be sent to the client-side via a variable.
 * 
 * @author davengo GmbH (Germany/Berlin, www.davengo.com)
 * 
 */
public class ClientEventList {

    /**
     * the list containing the registered client events (as strings for
     * client-side transfer)
     */
    private Map<String, Integer> clientEvents = null;

    /**
     * initializes the list if necessary
     */
    private void checkList() {
        if (clientEvents == null) {
            clientEvents = new HashMap<String, Integer>();
        }
    }

    /**
     * listens for the event <br>
     * <br>
     * increments the internal counter for the event by one
     * 
     * @param eventIdentifier
     *            the identifier of the event to listen for
     */
    public void listenEvent(String eventIdentifier) {
        checkList();
        if (!clientEvents.keySet().contains(eventIdentifier))
            clientEvents.put(eventIdentifier, 1);
        else
            clientEvents.put(eventIdentifier,
                    clientEvents.get(eventIdentifier) + 1);
    }

    /**
     * unlistens for an event <br>
     * <br>
     * decrements the internal counter by one, if 0 is reached the event is
     * removed from the event-list
     * 
     * @param eventIdentifier
     *            the identifier of the event to stop listening for
     */
    public void unlistenEvent(String eventIdentifier) {
        checkList();
        if (clientEvents.keySet().contains(eventIdentifier)) {
            clientEvents.put(eventIdentifier,
                    clientEvents.get(eventIdentifier) - 1);
            if (clientEvents.get(eventIdentifier) <= 0)
                clientEvents.remove(eventIdentifier);
        }
    }

    /**
     * @return a string array containing all registered events
     */
    public String[] getEvents() {
        if (clientEvents == null) {
            return new String[] {};
        }
        return clientEvents.keySet().toArray(new String[] {});
    }

}
