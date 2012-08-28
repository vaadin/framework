/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.terminal.gwt.client.metadata.NoDataException;
import com.vaadin.terminal.gwt.client.metadata.Type;
import com.vaadin.terminal.gwt.client.metadata.TypeData;

/**
 * An abstract implementation of Connector.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 * 
 */
public abstract class AbstractConnector implements ServerConnector,
StateChangeHandler {

    private ApplicationConnection connection;
    private String id;

    private HandlerManager handlerManager;
    private Map<String, HandlerManager> statePropertyHandlerManagers;
    private Map<String, Collection<ClientRpc>> rpcImplementations;
    private final boolean debugLogging = false;

    private SharedState state;
    private ServerConnector parent;

    /**
     * Temporary storage for last enabled state to be able to see if it has
     * changed. Can be removed once we are able to listen specifically for
     * enabled changes in the state. Widget.isEnabled() cannot be used as all
     * Widgets do not implement HasEnabled
     */
    private boolean lastEnabledState = true;
    private List<ServerConnector> children;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintable#getConnection()
     */
    @Override
    public final ApplicationConnection getConnection() {
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Connector#getId()
     */
    @Override
    public String getConnectorId() {
        return id;
    }

    /**
     * Called once by the framework to initialize the connector.
     * <p>
     * Note that the shared state is not yet available when this method is
     * called.
     * <p>
     * Connector classes should override {@link #init()} instead of this method.
     */
    @Override
    public final void doInit(String connectorId,
            ApplicationConnection connection) {
        this.connection = connection;
        id = connectorId;

        addStateChangeHandler(this);
        init();
    }

    /**
     * Called when the connector has been initialized. Override this method to
     * perform initialization of the connector.
     */
    // FIXME: It might make sense to make this abstract to force users to
    // use init instead of constructor, where connection and id has not yet been
    // set.
    protected void init() {

    }

    /**
     * Registers an implementation for a server to client RPC interface.
     * 
     * Multiple registrations can be made for a single interface, in which case
     * all of them receive corresponding RPC calls.
     * 
     * @param rpcInterface
     *            RPC interface
     * @param implementation
     *            implementation that should receive RPC calls
     * @param <T>
     *            The type of the RPC interface that is being registered
     */
    protected <T extends ClientRpc> void registerRpc(Class<T> rpcInterface,
            T implementation) {
        String rpcInterfaceId = rpcInterface.getName().replaceAll("\\$", ".");
        if (null == rpcImplementations) {
            rpcImplementations = new HashMap<String, Collection<ClientRpc>>();
        }
        if (null == rpcImplementations.get(rpcInterfaceId)) {
            rpcImplementations.put(rpcInterfaceId, new ArrayList<ClientRpc>());
        }
        rpcImplementations.get(rpcInterfaceId).add(implementation);
    }

    /**
     * Unregisters an implementation for a server to client RPC interface.
     * 
     * @param rpcInterface
     *            RPC interface
     * @param implementation
     *            implementation to unregister
     */
    protected <T extends ClientRpc> void unregisterRpc(Class<T> rpcInterface,
            T implementation) {
        String rpcInterfaceId = rpcInterface.getName().replaceAll("\\$", ".");
        if (null != rpcImplementations
                && null != rpcImplementations.get(rpcInterfaceId)) {
            rpcImplementations.get(rpcInterfaceId).remove(implementation);
        }
    }

    @Override
    public <T extends ClientRpc> Collection<T> getRpcImplementations(
            String rpcInterfaceId) {
        if (null == rpcImplementations) {
            return Collections.emptyList();
        }
        return (Collection<T>) rpcImplementations.get(rpcInterfaceId);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
        if (statePropertyHandlerManagers != null
                && event instanceof StateChangeEvent) {
            for (String property : ((StateChangeEvent) event)
                    .getChangedProperties()) {
                HandlerManager manager = statePropertyHandlerManagers
                        .get(property);
                if (manager != null) {
                    manager.fireEvent(event);
                }
            }
        }
    }

    protected HandlerManager ensureHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }

        return handlerManager;
    }

    @Override
    public HandlerRegistration addStateChangeHandler(StateChangeHandler handler) {
        return ensureHandlerManager()
                .addHandler(StateChangeEvent.TYPE, handler);
    }

    @Override
    public void removeStateChangeHandler(StateChangeHandler handler) {
        ensureHandlerManager().removeHandler(StateChangeEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addStateChangeHandler(String propertyName,
            StateChangeHandler handler) {
        return ensureHandlerManager(propertyName).addHandler(
                StateChangeEvent.TYPE, handler);
    }

    @Override
    public void removeStateChangeHandler(String propertyName,
            StateChangeHandler handler) {
        ensureHandlerManager(propertyName).removeHandler(StateChangeEvent.TYPE,
                handler);
    }

    private HandlerManager ensureHandlerManager(String propertyName) {
        if (statePropertyHandlerManagers == null) {
            statePropertyHandlerManagers = new HashMap<String, HandlerManager>();
        }
        HandlerManager manager = statePropertyHandlerManagers.get(propertyName);
        if (manager == null) {
            manager = new HandlerManager(this);
            statePropertyHandlerManagers.put(propertyName, manager);
        }
        return manager;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        if (debugLogging) {
            VConsole.log("State change event for "
                    + Util.getConnectorString(stateChangeEvent.getConnector())
                    + " received by " + Util.getConnectorString(this));
        }

        updateEnabledState(isEnabled());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ServerConnector#onUnregister()
     */
    @Override
    public void onUnregister() {
        if (debugLogging) {
            VConsole.log("Unregistered connector "
                    + Util.getConnectorString(this));
        }

    }

    /**
     * Returns the shared state object for this connector.
     * 
     * Override this method to define the shared state type for your connector.
     * 
     * @return the current shared state (never null)
     */
    @Override
    public SharedState getState() {
        if (state == null) {
            state = createState();
        }

        return state;
    }

    /**
     * Creates a state object with default values for this connector. The
     * created state object must be compatible with the return type of
     * {@link #getState()}. The default implementation creates a state object
     * using GWT.create() using the defined return type of {@link #getState()}.
     * 
     * @return A new state object
     */
    protected SharedState createState() {
        Type connectorType = TypeData.getType(getClass());
        try {
            Type stateType = connectorType.getMethod("getState")
                    .getReturnType();
            Object stateInstance = stateType.createInstance();
            return (SharedState) stateInstance;
        } catch (NoDataException e) {
            throw new IllegalStateException(
                    "There is no information about the state for "
                            + Util.getSimpleName(this)
                            + ". Did you remember to compile the right widgetset?",
                    e);
        }

    }

    @Override
    public ServerConnector getParent() {
        return parent;
    }

    @Override
    public void setParent(ServerConnector parent) {
        this.parent = parent;
    }

    @Override
    public List<ServerConnector> getChildren() {
        if (children == null) {
            return Collections.emptyList();
        }
        return children;
    }

    @Override
    public void setChildren(List<ServerConnector> children) {
        this.children = children;
    }

    @Override
    public boolean isEnabled() {
        if (!getState().isEnabled()) {
            return false;
        }

        if (getParent() == null) {
            return true;
        } else {
            return getParent().isEnabled();
        }
    }

    @Override
    public void updateEnabledState(boolean enabledState) {
        if (lastEnabledState == enabledState) {
            return;
        }
        lastEnabledState = enabledState;

        for (ServerConnector c : getChildren()) {
            // Update children as they might be affected by the enabled state of
            // their parent
            c.updateEnabledState(c.isEnabled());
        }
    }
}
