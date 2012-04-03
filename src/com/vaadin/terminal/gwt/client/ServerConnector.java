/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.Collection;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;

/**
 * Interface implemented by all client side classes that can be communicate with
 * the server. Classes implementing this interface are initialized by the
 * framework when needed and have the ability to communicate with the server.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface ServerConnector extends Connector {

    /**
     * Sets a new state for the connector.
     * 
     * @param state
     *            The new state
     * @deprecated This should be removed. Framework should update what is
     *             returned by getState() instead of setting a new state object.
     *             Note that this must be done either so that setState accepts a
     *             state object once (first time received from the server) or
     *             getState() in AbstractConnector uses a generated class to
     *             create the state object (like RpcProy.craete())
     */
    @Deprecated
    public void setState(SharedState state);

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

    /**
     * Adds a handler that is called whenever some part of the state has been
     * updated by the server.
     * 
     * @param handler
     *            The handler that should be added.
     * @return A handler registration reference that can be used to unregister
     *         the handler
     */
    public HandlerRegistration addStateChangeHandler(StateChangeHandler handler);

    /**
     * Sends the given event to all registered handlers.
     * 
     * @param event
     *            The event to send.
     */
    public void fireEvent(GwtEvent<?> event);

    /**
     * Event called when connector has been unregistered.
     */
    public void onUnregister();

}
