/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.SharedState;

/**
 * Interface implemented by all client side classes that can be communicate with
 * the server. Classes implementing this interface are initialized by the
 * framework when needed and have the ability to communicate with the server.
 * 
 * @author Vaadin Ltd
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
     * Adds a handler that is called whenever any part of the state has been
     * updated by the server.
     * 
     * @param handler
     *            The handler that should be added.
     * @return A handler registration reference that can be used to unregister
     *         the handler
     */
    public HandlerRegistration addStateChangeHandler(StateChangeHandler handler);

    /**
     * Removes a handler that is called whenever any part of the state has been
     * updated by the server.
     * 
     * @param handler
     *            The handler that should be removed.
     */
    public void removeStateChangeHandler(StateChangeHandler handler);

    /**
     * Adds a handler that is called whenever the given part of the state has
     * been updated by the server.
     * 
     * @param propertyName
     *            the name of the property for which the handler should be
     *            called
     * @param handler
     *            The handler that should be added.
     * @return A handler registration reference that can be used to unregister
     *         the handler
     */
    public HandlerRegistration addStateChangeHandler(String propertyName,
            StateChangeHandler handler);

    /**
     * Removes a handler that is called whenever any part of the state has been
     * updated by the server.
     * 
     * @param propertyName
     *            the name of the property for which the handler should be
     *            called
     * @param handler
     *            The handler that should be removed.
     */
    public void removeStateChangeHandler(String propertyName,
            StateChangeHandler handler);

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
    @Override
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

    /**
     * Gets the current shared state of the connector.
     * 
     * Note that state is considered an internal part of the connector. You
     * should not rely on the state object outside of the connector who owns it.
     * If you depend on the state of other connectors you should use their
     * public API instead of their state object directly.
     * 
     * @since 7.0.
     * @return state The shared state object. Can be any sub type of
     *         {@link SharedState}. Never null.
     */
    public SharedState getState();

    /**
     * Checks if an event listener has been registered on the server side for
     * the given event identifier.
     * 
     * @param eventIdentifier
     *            The identifier for the event
     * @return true if a listener has been registered on the server side, false
     *         otherwise
     */
    public boolean hasEventListener(String eventIdentifier);

}
