/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.util.List;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Interface implemented by all connectors that are capable of communicating
 * with the client side
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface ClientConnector extends Connector, RpcTarget {
    /**
     * Returns the list of pending server to client RPC calls and clears the
     * list.
     * 
     * @return an unmodifiable ordered list of pending server to client method
     *         calls (not null)
     * 
     * @since 7.0
     */
    public List<ClientMethodInvocation> retrievePendingRpcCalls();

    /**
     * Checks if the communicator is enabled. An enabled communicator is allowed
     * to receive messages from its counter-part.
     * 
     * @return true if the connector can receive messages, false otherwise
     */
    public boolean isConnectorEnabled();

    /**
     * Returns the type of the shared state for this connector
     * 
     * @return The type of the state. Must never return null.
     */
    public Class<? extends SharedState> getStateType();

}
