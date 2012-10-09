package com.vaadin.server;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.HttpSession;

/**
 * Stores and retrieves {@link VaadinServiceSession} instances and provides some
 * related information.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface VaadinServiceSessionStorage extends Serializable {
    /**
     * Loads a session if based on the data in the event. Returns null if no
     * session can be found. This method should try to avoid side effects, e.g.
     * not creating a {@link HttpSession} if there isn't already a session for
     * the request.
     * 
     * @param event
     *            a session storage event with data about the request
     * @return a Vaadin service session; or <code>null</code> if no existing
     *         session can be found
     */
    public VaadinServiceSession loadSession(SessionStorageEvent event);

    /**
     * Stores a session for later retrieval for the same user based on data in
     * the event.
     * 
     * @param session
     *            a session storage event with data about the request
     * @param event
     *            a session storage event
     */
    public void storeSession(VaadinServiceSession session,
            SessionStorageEvent event);

    /**
     * Removes a session from the storage based on data in the event.
     * 
     * @param session
     *            the Vaadin service session to remove
     * @param event
     *            a session storage event
     */
    public void removeSession(VaadinServiceSession session,
            SessionStorageEvent event);

    /**
     * Gets a lock that should be used to protect the session for concurrent
     * modification. Only {@link Lock#lock()} and {@link Lock#unlock()} are
     * required to be implemented in the returned instance.
     * 
     * @param session
     *            the Vaadin service session to get a lock for
     * @return a lock for synchronizing session access
     */
    public Lock getSessionLock(VaadinServiceSession session);

    /**
     * Returns the maximum time interval, in seconds, that the session will be
     * kept open between client accesses. After this interval, the session may
     * be removed from its storage. A negative time indicates the session should
     * never timeout.
     * 
     * @param event
     *            a session storage event with data about the request
     * @return an integer specifying the number of seconds this session remains
     *         open between client requests
     */
    public int getSessionStorageTime(SessionStorageEvent event);
}