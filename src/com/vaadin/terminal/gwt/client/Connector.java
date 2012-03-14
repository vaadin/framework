/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * TODO Add javadoc
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface Connector {
    /**
     * Gets the current shared state of the connector.
     * 
     * @return state
     */
    public SharedState getState();

    /**
     * Returns the id for this connector. This must always be what has been set
     * in {@link #doInit(String, ApplicationConnection)} and must never change.
     * 
     * @return The id for the connector.
     */
    public String getConnectorId();

    /**
     * Checks if the communicator is enabled. An enabled communicator is allowed
     * to receive messages from its counter-part.
     * 
     * @return true if the connector can receive messages, false otherwise
     */
    public boolean isConnectorEnabled();
}
