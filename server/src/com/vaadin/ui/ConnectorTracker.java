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
package com.vaadin.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractCommunicationManager;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.GlobalResourceHandler;

/**
 * A class which takes care of book keeping of {@link ClientConnector}s for a
 * UI.
 * <p>
 * Provides {@link #getConnector(String)} which can be used to lookup a
 * connector from its id. This is for framework use only and should not be
 * needed in applications.
 * </p>
 * <p>
 * Tracks which {@link ClientConnector}s are dirty so they can be updated to the
 * client when the following response is sent. A connector is dirty when an
 * operation has been performed on it on the server and as a result of this
 * operation new information needs to be sent to its
 * {@link com.vaadin.client.ServerConnector}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 * 
 */
public class ConnectorTracker implements Serializable {

    private final HashMap<String, ClientConnector> connectorIdToConnector = new HashMap<String, ClientConnector>();
    private Set<ClientConnector> dirtyConnectors = new HashSet<ClientConnector>();
    private Set<ClientConnector> uninitializedConnectors = new HashSet<ClientConnector>();

    private boolean writingResponse = false;

    private UI uI;
    private transient Map<ClientConnector, JSONObject> diffStates = new HashMap<ClientConnector, JSONObject>();

    /**
     * Gets a logger for this class
     * 
     * @return A logger instance for logging within this class
     * 
     */
    public static Logger getLogger() {
        return Logger.getLogger(ConnectorTracker.class.getName());
    }

    /**
     * Creates a new ConnectorTracker for the given uI. A tracker is always
     * attached to a uI and the uI cannot be changed during the lifetime of a
     * {@link ConnectorTracker}.
     * 
     * @param uI
     *            The uI to attach to. Cannot be null.
     */
    public ConnectorTracker(UI uI) {
        this.uI = uI;
    }

    /**
     * Register the given connector.
     * <p>
     * The lookup method {@link #getConnector(String)} only returns registered
     * connectors.
     * </p>
     * 
     * @param connector
     *            The connector to register.
     */
    public void registerConnector(ClientConnector connector) {
        String connectorId = connector.getConnectorId();
        ClientConnector previouslyRegistered = connectorIdToConnector
                .get(connectorId);
        if (previouslyRegistered == null) {
            connectorIdToConnector.put(connectorId, connector);
            uninitializedConnectors.add(connector);
            getLogger().fine(
                    "Registered " + connector.getClass().getSimpleName() + " ("
                            + connectorId + ")");
        } else if (previouslyRegistered != connector) {
            throw new RuntimeException("A connector with id " + connectorId
                    + " is already registered!");
        } else {
            getLogger().warning(
                    "An already registered connector was registered again: "
                            + connector.getClass().getSimpleName() + " ("
                            + connectorId + ")");
        }

    }

    /**
     * Unregister the given connector.
     * 
     * <p>
     * The lookup method {@link #getConnector(String)} only returns registered
     * connectors.
     * </p>
     * 
     * @param connector
     *            The connector to unregister
     */
    public void unregisterConnector(ClientConnector connector) {
        String connectorId = connector.getConnectorId();
        if (!connectorIdToConnector.containsKey(connectorId)) {
            getLogger().warning(
                    "Tried to unregister "
                            + connector.getClass().getSimpleName() + " ("
                            + connectorId + ") which is not registered");
            return;
        }
        if (connectorIdToConnector.get(connectorId) != connector) {
            throw new RuntimeException("The given connector with id "
                    + connectorId
                    + " is not the one that was registered for that id");
        }

        getLogger().fine(
                "Unregistered " + connector.getClass().getSimpleName() + " ("
                        + connectorId + ")");

        removeFromGlobalResourceHandler(connector);
        connectorIdToConnector.remove(connectorId);
        uninitializedConnectors.remove(connector);
        diffStates.remove(connector);
    }

    private void removeFromGlobalResourceHandler(ClientConnector connector) {
        GlobalResourceHandler globalResourceHandler = uI.getSession()
                .getGlobalResourceHandler(false);
        // Nothing to do if there is no handler
        if (globalResourceHandler != null) {
            globalResourceHandler.unregisterConnector(connector);
        }
    }

    /**
     * Checks whether the given connector has already been initialized in the
     * browser. The given connector should be registered with this connector
     * tracker.
     * 
     * @param connector
     *            the client connector to check
     * @return <code>true</code> if the initial state has previously been sent
     *         to the browser, <code>false</code> if the client-side doesn't
     *         already know anything about the connector.
     */
    public boolean isClientSideInitialized(ClientConnector connector) {
        assert connectorIdToConnector.get(connector.getConnectorId()) == connector : "Connector should be registered with this ConnectorTracker";
        return !uninitializedConnectors.contains(connector);
    }

    /**
     * Marks the given connector as initialized, meaning that the client-side
     * state has been initialized for the connector.
     * 
     * @see #isClientSideInitialized(ClientConnector)
     * 
     * @param connector
     *            the connector that should be marked as initialized
     */
    public void markClientSideInitialized(ClientConnector connector) {
        uninitializedConnectors.remove(connector);
    }

    /**
     * Marks all currently registered connectors as uninitialized. This should
     * be done when the client-side has been reset but the server-side state is
     * retained.
     * 
     * @see #isClientSideInitialized(ClientConnector)
     */
    public void markAllClientSidesUninitialized() {
        uninitializedConnectors.addAll(connectorIdToConnector.values());
        diffStates.clear();
    }

    /**
     * Gets a connector by its id.
     * 
     * @param connectorId
     *            The connector id to look for
     * @return The connector with the given id or null if no connector has the
     *         given id
     */
    public ClientConnector getConnector(String connectorId) {
        return connectorIdToConnector.get(connectorId);
    }

    /**
     * Cleans the connector map from all connectors that are no longer attached
     * to the application. This should only be called by the framework.
     */
    public void cleanConnectorMap() {
        // remove detached components from paintableIdMap so they
        // can be GC'ed
        Iterator<String> iterator = connectorIdToConnector.keySet().iterator();

        while (iterator.hasNext()) {
            String connectorId = iterator.next();
            ClientConnector connector = connectorIdToConnector.get(connectorId);
            if (getUIForConnector(connector) != uI) {
                // If connector is no longer part of this uI,
                // remove it from the map. If it is re-attached to the
                // application at some point it will be re-added through
                // registerConnector(connector)

                // This code should never be called as cleanup should take place
                // in detach()
                getLogger()
                        .warning(
                                "cleanConnectorMap unregistered connector "
                                        + getConnectorAndParentInfo(connector)
                                        + "). This should have been done when the connector was detached.");

                removeFromGlobalResourceHandler(connector);
                uninitializedConnectors.remove(connector);
                diffStates.remove(connector);
                iterator.remove();
            } else if (!AbstractCommunicationManager
                    .isConnectorVisibleToClient(connector)
                    && !uninitializedConnectors.contains(connector)) {
                uninitializedConnectors.add(connector);
                diffStates.remove(connector);
                getLogger().fine(
                        "cleanConnectorMap removed state for "
                                + getConnectorAndParentInfo(connector)
                                + " as it is not visible");
            }
        }

    }

    /**
     * Finds the uI that the connector is attached to.
     * 
     * @param connector
     *            The connector to lookup
     * @return The uI the connector is attached to or null if it is not attached
     *         to any uI.
     */
    private UI getUIForConnector(ClientConnector connector) {
        if (connector == null) {
            return null;
        }
        if (connector instanceof Component) {
            return ((Component) connector).getUI();
        }

        return getUIForConnector(connector.getParent());
    }

    /**
     * Mark the connector as dirty. This should not be done while the response
     * is being written.
     * 
     * @see #getDirtyConnectors()
     * @see #isWritingResponse()
     * 
     * @param connector
     *            The connector that should be marked clean.
     */
    public void markDirty(ClientConnector connector) {
        if (isWritingResponse()) {
            throw new IllegalStateException(
                    "A connector should not be marked as dirty while a response is being written.");
        }

        if (getLogger().isLoggable(Level.FINE)) {
            if (!dirtyConnectors.contains(connector)) {
                getLogger().fine(
                        getConnectorAndParentInfo(connector) + " "
                                + "is now dirty");
            }
        }

        dirtyConnectors.add(connector);
    }

    /**
     * Mark the connector as clean.
     * 
     * @param connector
     *            The connector that should be marked clean.
     */
    public void markClean(ClientConnector connector) {
        if (getLogger().isLoggable(Level.FINE)) {
            if (dirtyConnectors.contains(connector)) {
                getLogger().fine(
                        getConnectorAndParentInfo(connector) + " "
                                + "is no longer dirty");
            }
        }

        dirtyConnectors.remove(connector);
    }

    /**
     * Returns {@link #getConnectorString(ClientConnector)} for the connector
     * and its parent (if it has a parent).
     * 
     * @param connector
     *            The connector
     * @return A string describing the connector and its parent
     */
    private String getConnectorAndParentInfo(ClientConnector connector) {
        String message = getConnectorString(connector);
        if (connector.getParent() != null) {
            message += " (parent: " + getConnectorString(connector.getParent())
                    + ")";
        }
        return message;
    }

    /**
     * Returns a string with the connector name and id. Useful mostly for
     * debugging and logging.
     * 
     * @param connector
     *            The connector
     * @return A string that describes the connector
     */
    private String getConnectorString(ClientConnector connector) {
        if (connector == null) {
            return "(null)";
        }

        String connectorId;
        try {
            connectorId = connector.getConnectorId();
        } catch (RuntimeException e) {
            // This happens if the connector is not attached to the application.
            // SHOULD not happen in this case but theoretically can.
            connectorId = "@" + Integer.toHexString(connector.hashCode());
        }
        return connector.getClass().getName() + "(" + connectorId + ")";
    }

    /**
     * Mark all connectors in this uI as dirty.
     */
    public void markAllConnectorsDirty() {
        markConnectorsDirtyRecursively(uI);
        getLogger().fine("All connectors are now dirty");
    }

    /**
     * Mark all connectors in this uI as clean.
     */
    public void markAllConnectorsClean() {
        dirtyConnectors.clear();
        getLogger().fine("All connectors are now clean");
    }

    /**
     * Marks all visible connectors dirty, starting from the given connector and
     * going downwards in the hierarchy.
     * 
     * @param c
     *            The component to start iterating downwards from
     */
    private void markConnectorsDirtyRecursively(ClientConnector c) {
        if (c instanceof Component && !((Component) c).isVisible()) {
            return;
        }
        markDirty(c);
        for (ClientConnector child : AbstractClientConnector
                .getAllChildrenIterable(c)) {
            markConnectorsDirtyRecursively(child);
        }
    }

    /**
     * Returns a collection of all connectors which have been marked as dirty.
     * <p>
     * The state and pending RPC calls for dirty connectors are sent to the
     * client in the following request.
     * </p>
     * 
     * @return A collection of all dirty connectors for this uI. This list may
     *         contain invisible connectors.
     */
    public Collection<ClientConnector> getDirtyConnectors() {
        return dirtyConnectors;
    }

    public JSONObject getDiffState(ClientConnector connector) {
        return diffStates.get(connector);
    }

    public void setDiffState(ClientConnector connector, JSONObject diffState) {
        diffStates.put(connector, diffState);
    }

    public boolean isDirty(ClientConnector connector) {
        return dirtyConnectors.contains(connector);
    }

    /**
     * Checks whether the response is currently being written. Connectors can
     * not be marked as dirty when a response is being written.
     * 
     * @see #setWritingResponse(boolean)
     * @see #markDirty(ClientConnector)
     * 
     * @return <code>true</code> if the response is currently being written,
     *         <code>false</code> if outside the response writing phase.
     */
    public boolean isWritingResponse() {
        return writingResponse;
    }

    /**
     * Sets the current response write status. Connectors can not be marked as
     * dirty when the response is written.
     * 
     * @param writingResponse
     *            the new response status.
     * 
     * @see #markDirty(ClientConnector)
     * @see #isWritingResponse()
     * 
     * @throws IllegalArgumentException
     *             if the new response status is the same as the previous value.
     *             This is done to help detecting problems caused by missed
     *             invocations of this method.
     */
    public void setWritingResponse(boolean writingResponse) {
        if (this.writingResponse == writingResponse) {
            throw new IllegalArgumentException(
                    "The old value is same as the new value");
        }
        this.writingResponse = writingResponse;
    }

    /* Special serialization to JSONObjects which are not serializable */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Convert JSONObjects in diff state to String representation as
        // JSONObject is not serializable
        HashMap<ClientConnector, String> stringDiffStates = new HashMap<ClientConnector, String>(
                diffStates.size());
        for (ClientConnector key : diffStates.keySet()) {
            stringDiffStates.put(key, diffStates.get(key).toString());
        }
        out.writeObject(stringDiffStates);
    };

    /* Special serialization to JSONObjects which are not serializable */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        // Read String versions of JSONObjects and parse into JSONObjects as
        // JSONObject is not serializable
        diffStates = new HashMap<ClientConnector, JSONObject>();
        @SuppressWarnings("unchecked")
        HashMap<ClientConnector, String> stringDiffStates = (HashMap<ClientConnector, String>) in
                .readObject();
        diffStates = new HashMap<ClientConnector, JSONObject>();
        for (ClientConnector key : stringDiffStates.keySet()) {
            try {
                diffStates.put(key, new JSONObject(stringDiffStates.get(key)));
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

    }
}
