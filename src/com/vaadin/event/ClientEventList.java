/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <code>ClientEventList</code> class used to store the registered events so a
 * list of the required client event identifiers (that the client-side should
 * listen for and send to the server-side) can be sent to the client-side via a
 * variable.
 * 
 * @author davengo GmbH (Germany/Berlin, www.davengo.com)
 * @since 6.2
 * 
 */
public class ClientEventList {

    /**
     * the list containing the registered client events (as strings for
     * client-side transfer)
     * 
     * @since 6.2
     */
    private Map<String, Integer> clientEvents = null;

    /**
     * initializes the list if necessary
     * 
     * @since 6.2
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
     * 
     * @return true, if the event is newly added to the list<br>
     *         false, if the list already contained the event (the internal
     *         counter was incremented)
     * 
     * @since 6.2
     */
    public boolean listenEvent(String eventIdentifier) {
        checkList();
        if (!clientEvents.keySet().contains(eventIdentifier)) {
            clientEvents.put(eventIdentifier, 1);
            return true;
        } else {
            clientEvents.put(eventIdentifier,
                    clientEvents.get(eventIdentifier) + 1);
            return false;
        }
    }

    /**
     * unlistens for an event <br>
     * <br>
     * decrements the internal counter by one, if 0 is reached the event is
     * removed from the event-list
     * 
     * @param eventIdentifier
     *            the identifier of the event to stop listening for
     * @return true, if the event was removed from the list<br>
     *         false, if the event is hold in list (the internal counter was
     *         greater than zero)
     */
    public boolean unlistenEvent(String eventIdentifier) {
        checkList();
        if (clientEvents.keySet().contains(eventIdentifier)) {
            clientEvents.put(eventIdentifier,
                    clientEvents.get(eventIdentifier) - 1);
            if (clientEvents.get(eventIdentifier) <= 0) {
                clientEvents.remove(eventIdentifier);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * @return a string array containing all registered events
     * 
     * @since 6.2
     */
    public String[] getEvents() {
        if (clientEvents == null) {
            return new String[] {};
        }
        return clientEvents.keySet().toArray(new String[] {});
    }

}
