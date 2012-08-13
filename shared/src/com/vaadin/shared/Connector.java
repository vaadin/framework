/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared;

import java.io.Serializable;

import com.vaadin.shared.communication.SharedState;

/**
 * Interface implemented by all classes that are capable of communicating with
 * the server or the client side.
 * <p>
 * A connector consists of a shared state (server sets the state and
 * automatically communicates changes to the client) and the possibility to do
 * RPC calls either from the server to the client or from the client to the
 * server.
 * </p>
 * <p>
 * No classes should implement this interface directly, client side classes
 * wanting to communicate with server side should implement
 * {@link com.vaadin.terminal.gwt.client.ServerConnector} and server side
 * classes should implement
 * {@link com.vaadin.terminal.gwt.server.ClientConnector}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface Connector extends Serializable {
    /**
     * Gets the current shared state of the connector.
     * 
     * @since 7.0.
     * @return state The shared state object. Can be any sub type of
     *         {@link SharedState}. Never null.
     */
    public SharedState getState();

    /**
     * Returns the id for this connector. This is set by the framework and does
     * not change during the lifetime of a connector.
     * 
     * @return The id for the connector.
     */
    public String getConnectorId();

    /**
     * Gets the parent connector of this connector, or <code>null</code> if the
     * connector is not attached to any parent.
     * 
     * @return the parent connector, or <code>null</code> if there is no parent.
     */
    public Connector getParent();

}
