/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
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
     * Gets ApplicationConnection instance that created this connector.
     * 
     * @return The ApplicationConnection as set by
     *         {@link #doInit(String, ApplicationConnection)}
     */
    public ApplicationConnection getConnection();

    /**
     * Tests whether the connector is enabled or not. This method checks that
     * the connector is enabled in context, i.e. if the parent connector is
     * disabled, this method must return false.
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

    /**
     * Returns the parent of this connector. Can be null for only the root
     * connector.
     * 
     * @return The parent of this connector, as set by
     *         {@link #setParent(ServerConnector)}.
     */
    public ServerConnector getParent();

    /**
     * Sets the parent for this connector. This method should only be called by
     * the framework to ensure that the connector hierarchy on the client side
     * and the server side are in sync.
     * <p>
     * Note that calling this method does not fire a
     * {@link ConnectorHierarchyChangeEvent}. The event is fired only when the
     * whole hierarchy has been updated.
     * 
     * @param parent
     *            The new parent of the connector
     */
    public void setParent(ServerConnector parent);

    public void updateEnabledState(boolean enabledState);

    public void setChildren(List<ServerConnector> children);

    public List<ServerConnector> getChildren();
}
