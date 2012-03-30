/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.server.ClientConnector;

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
 * {@link ServerConnector} and server side classes should implement
 * {@link ClientConnector}.
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

}
