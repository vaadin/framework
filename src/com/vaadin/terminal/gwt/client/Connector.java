/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.Collection;

import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Interface implemented by all client side classes that can be communicate with
 * the server. Classes implementing this interface are initialized by the
 * framework when needed and have the ability to communicate with the server.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface Connector {
    /**
     * TODO
     * 
     * @param uidl
     * @param client
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client);

    /**
     * Gets the current shared state of the connector.
     * 
     * @return state
     */
    public SharedState getState();

    /**
     * Sets a new state for the connector.
     * 
     * @param state
     *            The new state
     * @deprecated This should be removed. Framework should update what is
     *             returned by getState() instead of setting a new state object.
     */
    @Deprecated
    public void setState(SharedState state);

    /**
     * Returns the id for this connector. This must always be what has been set
     * in {@link #doInit(String, ApplicationConnection)} and must never change.
     * 
     * @return The id for the connector.
     */
    public String getId();

    /**
     * Gets ApplicationConnection instance that created this connector.
     * 
     * @return The ApplicationConnection as set by
     *         {@link #doInit(String, ApplicationConnection)}
     */
    public ApplicationConnection getConnection();

    /**
     * Tests whether the connector is enabled or not. Disabled connectors will
     * ignore all attempts at communications. Received messages will be
     * discarded. This method must check that the connector is enabled in
     * context, that is if it's parent is disabled, this method must return
     * false.
     * 
     * @return true if the connector is enabled, false otherwise
     */
    public boolean isEnabled();

    /**
     * 
     * Called once by the framework to initialize the connector.
     * <p>
     * Note that the shared state is not yet available at this point nor any
     * hierarchy information.
     */
    public void doInit(String connectorId, ApplicationConnection connection);

    /**
     * For internal use by the framework: returns the registered RPC
     * implementations for an RPC interface identifier.
     * 
     * TODO interface identifier type or format may change
     * 
     * @param rpcInterfaceId
     *            RPC interface identifier: fully qualified interface type name
     * @return RPC interface implementations registered for an RPC interface,
     *         not null
     */
    public <T extends ClientRpc> Collection<T> getRpcImplementations(
            String rpcInterfaceId);

}
