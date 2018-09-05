/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.MarkedAsDirtyConnectorEvent;
import com.vaadin.event.MarkedAsDirtyListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.DragAndDropService;
import com.vaadin.server.GlobalResourceHandler;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.communication.ConnectorHierarchyWriter;
import com.vaadin.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonException;
import elemental.json.JsonObject;

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

    private final Map<String, ClientConnector> connectorIdToConnector = new HashMap<>();
    private final Set<ClientConnector> dirtyConnectors = new HashSet<>();
    private final Set<ClientConnector> uninitializedConnectors = new HashSet<>();

    private List<MarkedAsDirtyListener> markedDirtyListeners = new ArrayList<>(
            0);

    /**
     * Connectors that have been unregistered and should be cleaned up the next
     * time {@link #cleanConnectorMap(boolean)} is invoked unless they have been
     * registered again.
     */
    private final Set<ClientConnector> unregisteredConnectors = new HashSet<>();

    private boolean writingResponse = false;

    private final UI uI;
    private transient Map<ClientConnector, JsonObject> diffStates = new HashMap<>();

    /** Maps connectorIds to a map of named StreamVariables */
    private Map<String, Map<String, StreamVariable>> pidToNameToStreamVariable;

    private Map<StreamVariable, String> streamVariableToSeckey;

    private int currentSyncId = 0;

    /**
     * Gets a logger for this class
     *
     * @return A logger instance for logging within this class
     *
     */
    private static Logger getLogger() {
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
        boolean wasUnregistered = unregisteredConnectors.remove(connector);

        String connectorId = connector.getConnectorId();
        ClientConnector previouslyRegistered = connectorIdToConnector
                .get(connectorId);
        if (previouslyRegistered == null) {
            connectorIdToConnector.put(connectorId, connector);
            uninitializedConnectors.add(connector);
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(Level.FINE, "Registered {0} ({1})",
                        new Object[] { connector.getClass().getSimpleName(),
                                connectorId });
            }
        } else if (previouslyRegistered != connector) {
            throw new RuntimeException("A connector with id " + connectorId
                    + " is already registered!");
        } else if (!wasUnregistered) {
            getLogger().log(Level.WARNING,
                    "An already registered connector was registered again: {0} ({1})",
                    new Object[] { connector.getClass().getSimpleName(),
                            connectorId });
        }
        dirtyConnectors.add(connector);
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
            getLogger().log(Level.WARNING,
                    "Tried to unregister {0} ({1}) which is not registered",
                    new Object[] { connector.getClass().getSimpleName(),
                            connectorId });
            return;
        }
        if (connectorIdToConnector.get(connectorId) != connector) {
            throw new RuntimeException("The given connector with id "
                    + connectorId
                    + " is not the one that was registered for that id");
        }

        dirtyConnectors.remove(connector);

        if (!isClientSideInitialized(connector)) {
            // Client side has never known about this connector so there is no
            // point in tracking it
            removeUnregisteredConnector(connector,
                    uI.getSession().getGlobalResourceHandler(false));
        } else if (unregisteredConnectors.add(connector)) {
            // Client side knows about the connector, track it for a while if it
            // becomes reattached
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(Level.FINE, "Unregistered {0} ({1})",
                        new Object[] { connector.getClass().getSimpleName(),
                                connectorId });
            }
        } else {
            getLogger().log(Level.WARNING,
                    "Unregistered {0} ({1}) that was already unregistered.",
                    new Object[] { connector.getClass().getSimpleName(),
                            connectorId });
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
        assert connectorIdToConnector.get(connector
                .getConnectorId()) == connector : "Connector should be registered with this ConnectorTracker";
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
        ClientConnector connector = connectorIdToConnector.get(connectorId);
        // Ignore connectors that have been unregistered but not yet cleaned up
        if (unregisteredConnectors.contains(connector)) {
            return null;
        } else if (connector != null) {
            return connector;
        } else {
            DragAndDropService service = uI.getSession()
                    .getDragAndDropService();
            if (connectorId.equals(service.getConnectorId())) {
                return service;
            }
        }
        return null;
    }

    /**
     * Cleans the connector map from all connectors that are no longer attached
     * to the application if there are dirty connectors or the force flag is
     * true. This should only be called by the framework.
     *
     * @param force
     *            {@code true} to force cleaning
     * @since 8.2
     */
    public void cleanConnectorMap(boolean force) {
        if (force || !dirtyConnectors.isEmpty()) {
            cleanConnectorMap();
        }
    }

    /**
     * Cleans the connector map from all connectors that are no longer attached
     * to the application. This should only be called by the framework.
     *
     * @deprecated use {@link #cleanConnectorMap(boolean)} instead
     */
    @Deprecated
    public void cleanConnectorMap() {
        removeUnregisteredConnectors();

        cleanStreamVariables();

        // Do this expensive check only with assertions enabled
        assert isHierarchyComplete() : "The connector hierarchy is corrupted. "
                + "Check for missing calls to super.setParent(), super.attach() and super.detach() "
                + "and that all custom component containers call child.setParent(this) when a child is added and child.setParent(null) when the child is no longer used. "
                + "See previous log messages for details.";

        Iterator<ClientConnector> iterator = connectorIdToConnector.values()
                .iterator();
        GlobalResourceHandler globalResourceHandler = uI.getSession()
                .getGlobalResourceHandler(false);
        while (iterator.hasNext()) {
            ClientConnector connector = iterator.next();
            assert connector != null;
            if (connector.getUI() != uI) {
                // If connector is no longer part of this uI,
                // remove it from the map. If it is re-attached to the
                // application at some point it will be re-added through
                // registerConnector(connector)
                // This code should never be called as cleanup should take place
                // in detach()
                getLogger().log(Level.WARNING,
                        "cleanConnectorMap unregistered connector {0}. This should have been done when the connector was detached.",
                        getConnectorAndParentInfo(connector));
                if (globalResourceHandler != null) {
                    globalResourceHandler.unregisterConnector(connector);
                }
                uninitializedConnectors.remove(connector);
                diffStates.remove(connector);
                iterator.remove();
            } else if (!uninitializedConnectors.contains(connector)
                    && !LegacyCommunicationManager
                            .isConnectorVisibleToClient(connector)) {
                // Connector was visible to the client but is no longer (e.g.
                // setVisible(false) has been called or SelectiveRenderer tells
                // it's no longer shown) -> make sure that the full state is
                // sent again when/if made visible
                uninitializedConnectors.add(connector);
                diffStates.remove(connector);
                assert isRemovalSentToClient(connector) : "Connector "
                        + connector + " (id = " + connector.getConnectorId()
                        + ") is no longer visible to the client, but no corresponding hierarchy change was sent.";
                if (getLogger().isLoggable(Level.FINE)) {
                    getLogger().log(Level.FINE,
                            "cleanConnectorMap removed state for {0} as it is not visible",
                            getConnectorAndParentInfo(connector));
                }
            }
        }
    }

    private boolean isRemovalSentToClient(ClientConnector connector) {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request == null) {
            // Probably run from a unit test without normal request handling
            return true;
        }

        String attributeName = ConnectorHierarchyWriter.class.getName()
                + ".hierarchyInfo";
        Object hierarchyInfoObj = request.getAttribute(attributeName);
        if (hierarchyInfoObj instanceof JsonObject) {
            JsonObject hierachyInfo = (JsonObject) hierarchyInfoObj;

            ClientConnector firstVisibleParent = findFirstVisibleParent(
                    connector);
            if (firstVisibleParent == null) {
                // Connector is detached, not our business
                return true;
            }

            if (!hierachyInfo.hasKey(firstVisibleParent.getConnectorId())) {
                /*
                 * No hierarchy change about to be sent, but this might be
                 * because of an optimization that omits explicit hierarchy
                 * changes for empty connectors that have state changes.
                 */
                if (hasVisibleChild(firstVisibleParent)) {
                    // Not the optimization case if the parent has visible
                    // children
                    return false;
                }

                attributeName = ConnectorHierarchyWriter.class.getName()
                        + ".stateUpdateConnectors";
                Object stateUpdateConnectorsObj = request
                        .getAttribute(attributeName);
                if (stateUpdateConnectorsObj instanceof Set<?>) {
                    Set<?> stateUpdateConnectors = (Set<?>) stateUpdateConnectorsObj;
                    if (!stateUpdateConnectors
                            .contains(firstVisibleParent.getConnectorId())) {
                        // Not the optimization case if the parent is not marked
                        // as dirty
                        return false;
                    }
                } else {
                    getLogger().warning("Request attribute " + attributeName
                            + " is not a Set");
                }
            }
        } else {
            getLogger().warning("Request attribute " + attributeName
                    + " is not a JsonObject");
        }

        return true;
    }

    private static boolean hasVisibleChild(ClientConnector parent) {
        Iterable<? extends ClientConnector> iterable = AbstractClientConnector
                .getAllChildrenIterable(parent);
        for (ClientConnector child : iterable) {
            if (LegacyCommunicationManager.isConnectorVisibleToClient(child)) {
                return true;
            }
        }
        return false;
    }

    private ClientConnector findFirstVisibleParent(ClientConnector connector) {
        while (connector != null) {
            connector = connector.getParent();
            if (LegacyCommunicationManager
                    .isConnectorVisibleToClient(connector)) {
                return connector;
            }
        }
        return null;
    }

    /**
     * Removes all references and information about connectors marked as
     * unregistered.
     *
     */
    private void removeUnregisteredConnectors() {
        GlobalResourceHandler globalResourceHandler = uI.getSession()
                .getGlobalResourceHandler(false);

        for (ClientConnector connector : unregisteredConnectors) {
            removeUnregisteredConnector(connector, globalResourceHandler);
        }
        unregisteredConnectors.clear();
    }

    /**
     * Removes all references and information about the given connector, which
     * must not be registered.
     *
     * @param connector
     * @param globalResourceHandler
     */
    private void removeUnregisteredConnector(ClientConnector connector,
            GlobalResourceHandler globalResourceHandler) {
        ClientConnector removedConnector = connectorIdToConnector
                .remove(connector.getConnectorId());
        assert removedConnector == connector;

        if (globalResourceHandler != null) {
            globalResourceHandler.unregisterConnector(connector);
        }
        uninitializedConnectors.remove(connector);
        diffStates.remove(connector);
    }

    /**
     * Checks that the connector hierarchy is consistent.
     *
     * @return <code>true</code> if the hierarchy is consistent,
     *         <code>false</code> otherwise
     * @since 8.1
     */
    private boolean isHierarchyComplete() {
        boolean noErrors = true;

        Set<ClientConnector> danglingConnectors = new HashSet<>(
                connectorIdToConnector.values());

        LinkedList<ClientConnector> stack = new LinkedList<>();
        stack.add(uI);
        while (!stack.isEmpty()) {
            ClientConnector connector = stack.pop();
            danglingConnectors.remove(connector);

            Iterable<? extends ClientConnector> children = AbstractClientConnector
                    .getAllChildrenIterable(connector);
            for (ClientConnector child : children) {
                stack.add(child);

                if (!connector.equals(child.getParent())) {
                    noErrors = false;
                    getLogger().log(Level.WARNING,
                            "{0} claims that {1} is its child, but the child claims {2} is its parent.",
                            new Object[] { getConnectorString(connector),
                                    getConnectorString(child),
                                    getConnectorString(child.getParent()) });
                }
            }
        }

        for (ClientConnector dangling : danglingConnectors) {
            noErrors = false;
            getLogger().log(Level.WARNING,
                    "{0} claims that {1} is its parent, but the parent does not acknowledge the parenthood.",
                    new Object[] { getConnectorString(dangling),
                            getConnectorString(dangling.getParent()) });
        }

        return noErrors;
    }

    /**
     * Mark the connector as dirty and notifies any marked as dirty listeners.
     * This should not be done while the response is being written.
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
            if (!isDirty(connector)) {
                getLogger().log(Level.FINE, "{0} is now dirty",
                        getConnectorAndParentInfo(connector));
            }
        }

        if (!isDirty(connector)) {
            notifyMarkedAsDirtyListeners(connector);
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
                getLogger().log(Level.FINE, "{0} is no longer dirty",
                        getConnectorAndParentInfo(connector));
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

    /**
     * Checks if there a dirty connectors.
     *
     * @return true if there are dirty connectors, false otherwise
     */
    public boolean hasDirtyConnectors() {
        return !getDirtyConnectors().isEmpty();
    }

    /**
     * Returns a collection of those {@link #getDirtyConnectors() dirty
     * connectors} that are actually visible to the client.
     *
     * @return A list of dirty and visible connectors.
     */
    public ArrayList<ClientConnector> getDirtyVisibleConnectors() {
        Collection<ClientConnector> dirtyConnectors = getDirtyConnectors();
        ArrayList<ClientConnector> dirtyVisibleConnectors = new ArrayList<>(
                dirtyConnectors.size());
        for (ClientConnector c : dirtyConnectors) {
            if (LegacyCommunicationManager.isConnectorVisibleToClient(c)) {
                dirtyVisibleConnectors.add(c);
            }
        }
        return dirtyVisibleConnectors;
    }

    public JsonObject getDiffState(ClientConnector connector) {
        assert getConnector(connector.getConnectorId()) == connector;
        return diffStates.get(connector);
    }

    public void setDiffState(ClientConnector connector, JsonObject diffState) {
        assert getConnector(connector.getConnectorId()) == connector;
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
     * <p>
     * This method has a side-effect of incrementing the sync id by one (see
     * {@link #getCurrentSyncId()}), if {@link #isWritingResponse()} returns
     * <code>true</code> and <code>writingResponse</code> is set to
     * <code>false</code>.
     *
     * @param writingResponse
     *            the new response status.
     *
     * @see #markDirty(ClientConnector)
     * @see #isWritingResponse()
     * @see #getCurrentSyncId()
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

        /*
         * the right hand side of the && is unnecessary here because of the
         * if-clause above, but rigorous coding is always rigorous coding.
         */
        if (!writingResponse && this.writingResponse) {
            // Bump sync id when done writing - the client is not expected to
            // know about anything happening after this moment.
            currentSyncId++;
        }
        this.writingResponse = writingResponse;
    }

    /* Special serialization to JsonObjects which are not serializable */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Convert JsonObjects in diff state to String representation as
        // JsonObject is not serializable
        Map<ClientConnector, String> stringDiffStates = new HashMap<>(
                diffStates.size() * 2);
        for (ClientConnector key : diffStates.keySet()) {
            stringDiffStates.put(key, diffStates.get(key).toString());
        }
        out.writeObject(stringDiffStates);
    }

    /* Special serialization to JsonObjects which are not serializable */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Read String versions of JsonObjects and parse into JsonObjects as
        // JsonObject is not serializable
        diffStates = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<ClientConnector, String> stringDiffStates = (HashMap<ClientConnector, String>) in
                .readObject();
        diffStates = new HashMap<>(stringDiffStates.size() * 2);
        for (ClientConnector key : stringDiffStates.keySet()) {
            try {
                diffStates.put(key, Json.parse(stringDiffStates.get(key)));
            } catch (JsonException e) {
                throw new IOException(e);
            }
        }

    }

    /**
     * Checks if the indicated connector has a StreamVariable of the given name
     * and returns the variable if one is found.
     *
     * @param connectorId
     * @param variableName
     * @return variable if a matching one exists, otherwise null
     */
    public StreamVariable getStreamVariable(String connectorId,
            String variableName) {
        if (pidToNameToStreamVariable == null) {
            return null;
        }
        Map<String, StreamVariable> map = pidToNameToStreamVariable
                .get(connectorId);
        if (map == null) {
            return null;
        }
        StreamVariable streamVariable = map.get(variableName);
        return streamVariable;
    }

    /**
     * Adds a StreamVariable of the given name to the indicated connector.
     *
     * @param connectorId
     * @param variableName
     * @param variable
     */
    public void addStreamVariable(String connectorId, String variableName,
            StreamVariable variable) {
        assert getConnector(connectorId) != null;
        if (pidToNameToStreamVariable == null) {
            pidToNameToStreamVariable = new HashMap<>();
        }
        Map<String, StreamVariable> nameToStreamVariable = pidToNameToStreamVariable
                .get(connectorId);
        if (nameToStreamVariable == null) {
            nameToStreamVariable = new HashMap<>();
            pidToNameToStreamVariable.put(connectorId, nameToStreamVariable);
        }
        nameToStreamVariable.put(variableName, variable);

        if (streamVariableToSeckey == null) {
            streamVariableToSeckey = new HashMap<>();
        }
        String seckey = streamVariableToSeckey.get(variable);
        if (seckey == null) {
            /*
             * Despite section 6 of RFC 4122, this particular use of UUID *is*
             * adequate for security capabilities. Type 4 UUIDs contain 122 bits
             * of random data, and UUID.randomUUID() is defined to use a
             * cryptographically secure random generator.
             */
            seckey = UUID.randomUUID().toString();
            streamVariableToSeckey.put(variable, seckey);
        }
    }

    /**
     * Removes StreamVariables that belong to connectors that are no longer
     * attached to the session.
     */
    private void cleanStreamVariables() {
        if (pidToNameToStreamVariable != null) {
            ConnectorTracker connectorTracker = uI.getConnectorTracker();
            Iterator<String> iterator = pidToNameToStreamVariable.keySet()
                    .iterator();
            while (iterator.hasNext()) {
                String connectorId = iterator.next();
                if (connectorTracker.getConnector(connectorId) == null) {
                    // Owner is no longer attached to the session
                    Map<String, StreamVariable> removed = pidToNameToStreamVariable
                            .get(connectorId);
                    for (String key : removed.keySet()) {
                        streamVariableToSeckey.remove(removed.get(key));
                    }
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Removes any StreamVariable of the given name from the indicated
     * connector.
     *
     * @param connectorId
     * @param variableName
     */
    public void cleanStreamVariable(String connectorId, String variableName) {
        if (pidToNameToStreamVariable == null) {
            return;
        }
        Map<String, StreamVariable> nameToStreamVar = pidToNameToStreamVariable
                .get(connectorId);
        StreamVariable streamVar = nameToStreamVar.remove(variableName);
        streamVariableToSeckey.remove(streamVar);
        if (nameToStreamVar.isEmpty()) {
            pidToNameToStreamVariable.remove(connectorId);
        }
    }

    /**
     * Returns the security key associated with the given StreamVariable.
     *
     * @param variable
     * @return matching security key if one exists, null otherwise
     */
    public String getSeckey(StreamVariable variable) {
        if (streamVariableToSeckey == null) {
            return null;
        }
        return streamVariableToSeckey.get(variable);
    }

    /**
     * Gets the most recently generated server sync id.
     * <p>
     * The sync id is incremented by one whenever a new response is being
     * written. This id is then sent over to the client. The client then adds
     * the most recent sync id to each communication packet it sends back to the
     * server. This way, the server knows at what state the client is when the
     * packet is sent. If the state has changed on the server side since that,
     * the server can try to adjust the way it handles the actions from the
     * client side.
     * <p>
     * The sync id value <code>-1</code> is ignored to facilitate testing with
     * pre-recorded requests.
     *
     * @see #setWritingResponse(boolean)
     * @see #connectorWasPresentAsRequestWasSent(String, long)
     * @since 7.2
     * @return the current sync id
     */
    public int getCurrentSyncId() {
        return currentSyncId;
    }

    /**
     * Add a marked as dirty listener that will be called when a client
     * connector is marked as dirty.
     *
     * @param listener
     *            listener to add
     * @since 8.4
     * @return registration for removing listener registration
     */
    public Registration addMarkedAsDirtyListener(
            MarkedAsDirtyListener listener) {
        markedDirtyListeners.add(listener);
        return () -> markedDirtyListeners.remove(listener);
    }

    /**
     * Notify all registered MarkedAsDirtyListeners the given client connector
     * has been marked as dirty.
     *
     * @param connector
     *            client connector marked as dirty
     * @since 8.4
     */
    public void notifyMarkedAsDirtyListeners(ClientConnector connector) {
        MarkedAsDirtyConnectorEvent event = new MarkedAsDirtyConnectorEvent(
                connector, uI);
        new ArrayList<>(markedDirtyListeners).forEach(listener -> {
            listener.connectorMarkedAsDirty(event);
        });
    }

}
