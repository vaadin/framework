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
package com.vaadin.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.DragAndDropService;
import com.vaadin.server.GlobalResourceHandler;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.StreamVariable;

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

    /**
     * Connectors that have been unregistered and should be cleaned up the next
     * time {@link #cleanConnectorMap()} is invoked unless they have been
     * registered again.
     */
    private final Set<ClientConnector> unregisteredConnectors = new HashSet<ClientConnector>();

    private boolean writingResponse = false;

    private UI uI;
    private transient Map<ClientConnector, JSONObject> diffStates = new HashMap<ClientConnector, JSONObject>();

    /** Maps connectorIds to a map of named StreamVariables */
    private Map<String, Map<String, StreamVariable>> pidToNameToStreamVariable;

    private Map<StreamVariable, String> streamVariableToSeckey;

    private int currentSyncId = 0;

    /**
     * Map to track on which syncId each connector was removed.
     *
     * @see #getCurrentSyncId()
     * @see #cleanConcurrentlyRemovedConnectorIds(long)
     */
    private TreeMap<Integer, Set<String>> syncIdToUnregisteredConnectorIds = new TreeMap<Integer, Set<String>>();

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
        boolean wasUnregistered = unregisteredConnectors.remove(connector);

        String connectorId = connector.getConnectorId();
        ClientConnector previouslyRegistered = connectorIdToConnector
                .get(connectorId);
        if (previouslyRegistered == null) {
            connectorIdToConnector.put(connectorId, connector);
            uninitializedConnectors.add(connector);
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(
                        Level.FINE,
                        "Registered {0} ({1})",
                        new Object[] { connector.getClass().getSimpleName(),
                                connectorId });
            }
        } else if (previouslyRegistered != connector) {
            throw new RuntimeException("A connector with id " + connectorId
                    + " is already registered!");
        } else if (!wasUnregistered) {
            getLogger()
                    .log(Level.WARNING,
                            "An already registered connector was registered again: {0} ({1})",
                            new Object[] {
                                    connector.getClass().getSimpleName(),
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
            getLogger().log(
                    Level.WARNING,
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

        Set<String> unregisteredConnectorIds = syncIdToUnregisteredConnectorIds
                .get(currentSyncId);
        if (unregisteredConnectorIds == null) {
            unregisteredConnectorIds = new HashSet<String>();
            syncIdToUnregisteredConnectorIds.put(currentSyncId,
                    unregisteredConnectorIds);
        }
        unregisteredConnectorIds.add(connectorId);

        dirtyConnectors.remove(connector);
        if (unregisteredConnectors.add(connector)) {
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(
                        Level.FINE,
                        "Unregistered {0} ({1})",
                        new Object[] { connector.getClass().getSimpleName(),
                                connectorId });
            }
        } else {
            getLogger().log(
                    Level.WARNING,
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
     * to the application. This should only be called by the framework.
     */
    public void cleanConnectorMap() {
        if (!unregisteredConnectors.isEmpty()) {
            removeUnregisteredConnectors();
        }

        // Do this expensive check only with assertions enabled
        assert isHierarchyComplete() : "The connector hierarchy is corrupted. "
                + "Check for missing calls to super.setParent(), super.attach() and super.detach() "
                + "and that all custom component containers call child.setParent(this) when a child is added and child.setParent(null) when the child is no longer used. "
                + "See previous log messages for details.";

        // remove detached components from paintableIdMap so they
        // can be GC'ed
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

                getLogger()
                        .log(Level.WARNING,
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
                uninitializedConnectors.add(connector);
                diffStates.remove(connector);
                if (getLogger().isLoggable(Level.FINE)) {
                    getLogger()
                            .log(Level.FINE,
                                    "cleanConnectorMap removed state for {0} as it is not visible",
                                    getConnectorAndParentInfo(connector));
                }
            }
        }

        cleanStreamVariables();
    }

    private void removeUnregisteredConnectors() {
        GlobalResourceHandler globalResourceHandler = uI.getSession()
                .getGlobalResourceHandler(false);

        for (ClientConnector connector : unregisteredConnectors) {
            ClientConnector removedConnector = connectorIdToConnector
                    .remove(connector.getConnectorId());
            assert removedConnector == connector;

            if (globalResourceHandler != null) {
                globalResourceHandler.unregisterConnector(connector);
            }
            uninitializedConnectors.remove(connector);
            diffStates.remove(connector);
        }
        unregisteredConnectors.clear();
    }

    private boolean isHierarchyComplete() {
        boolean noErrors = true;

        Set<ClientConnector> danglingConnectors = new HashSet<ClientConnector>(
                connectorIdToConnector.values());

        LinkedList<ClientConnector> stack = new LinkedList<ClientConnector>();
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
                    getLogger()
                            .log(Level.WARNING,
                                    "{0} claims that {1} is its child, but the child claims {2} is its parent.",
                                    new Object[] {
                                            getConnectorString(connector),
                                            getConnectorString(child),
                                            getConnectorString(child
                                                    .getParent()) });
                }
            }
        }

        for (ClientConnector dangling : danglingConnectors) {
            noErrors = false;
            getLogger()
                    .log(Level.WARNING,
                            "{0} claims that {1} is its parent, but the parent does not acknowledge the parenthood.",
                            new Object[] { getConnectorString(dangling),
                                    getConnectorString(dangling.getParent()) });
        }

        return noErrors;
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
                getLogger().log(Level.FINE, "{0} is now dirty",
                        getConnectorAndParentInfo(connector));
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
        ArrayList<ClientConnector> dirtyVisibleConnectors = new ArrayList<ClientConnector>(
                dirtyConnectors.size());
        for (ClientConnector c : dirtyConnectors) {
            if (LegacyCommunicationManager.isConnectorVisibleToClient(c)) {
                dirtyVisibleConnectors.add(c);
            }
        }
        return dirtyVisibleConnectors;
    }

    public JSONObject getDiffState(ClientConnector connector) {
        assert getConnector(connector.getConnectorId()) == connector;
        return diffStates.get(connector);
    }

    public void setDiffState(ClientConnector connector, JSONObject diffState) {
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
     * <code>false</code> and <code>writingResponse</code> is set to
     * <code>true</code>.
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
        if (writingResponse && !this.writingResponse) {
            currentSyncId++;
        }
        this.writingResponse = writingResponse;
    }

    /* Special serialization to JSONObjects which are not serializable */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Convert JSONObjects in diff state to String representation as
        // JSONObject is not serializable
        HashMap<ClientConnector, String> stringDiffStates = new HashMap<ClientConnector, String>(
                diffStates.size() * 2);
        for (ClientConnector key : diffStates.keySet()) {
            stringDiffStates.put(key, diffStates.get(key).toString());
        }
        out.writeObject(stringDiffStates);
    }

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
        diffStates = new HashMap<ClientConnector, JSONObject>(
                stringDiffStates.size() * 2);
        for (ClientConnector key : stringDiffStates.keySet()) {
            try {
                diffStates.put(key, new JSONObject(stringDiffStates.get(key)));
            } catch (JSONException e) {
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
            pidToNameToStreamVariable = new HashMap<String, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToStreamVariable = pidToNameToStreamVariable
                .get(connectorId);
        if (nameToStreamVariable == null) {
            nameToStreamVariable = new HashMap<String, StreamVariable>();
            pidToNameToStreamVariable.put(connectorId, nameToStreamVariable);
        }
        nameToStreamVariable.put(variableName, variable);

        if (streamVariableToSeckey == null) {
            streamVariableToSeckey = new HashMap<StreamVariable, String>();
        }
        String seckey = streamVariableToSeckey.get(variable);
        if (seckey == null) {
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
        nameToStreamVar.remove(variableName);
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
     * Check whether a connector was present on the client when the it was
     * creating this request, but was removed server-side before the request
     * arrived.
     *
     * @since 7.2
     * @param connectorId
     *            The connector id to check for whether it was removed
     *            concurrently or not.
     * @param lastSyncIdSeenByClient
     *            the most recent sync id the client has seen at the time the
     *            request was sent, or -1 to ignore potential problems
     * @return <code>true</code> if the connector was removed before the client
     *         had a chance to react to it.
     */
    public boolean connectorWasPresentAsRequestWasSent(String connectorId,
            long lastSyncIdSeenByClient) {

        assert getConnector(connectorId) == null : "Connector " + connectorId
                + " is still attached";

        boolean clientRequestIsTooOld = lastSyncIdSeenByClient < currentSyncId
                && lastSyncIdSeenByClient != -1;
        if (clientRequestIsTooOld) {
            /*
             * The headMap call is present here because we're only interested in
             * connectors removed "in the past" (i.e. the server has removed
             * them before the client ever knew about that), since those are the
             * ones that we choose to handle as a special case.
             */
            /*-
             *   Server                          Client
             * [#1 add table] ---------.
             *                          \
             * [push: #2 remove table]-. `--> [adding table, storing #1]
             *                          \  .- [table from request #1 needs more data]
             *                           \/
             *                           /`-> [removing table, storing #2]
             * [#1 < #2 - ignoring] <---´
             */
            for (Set<String> unregisteredConnectors : syncIdToUnregisteredConnectorIds
                    .headMap(currentSyncId).values()) {
                if (unregisteredConnectors.contains(connectorId)) {
                    return true;
                }
            }
        }

        return false;
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
     * Maintains the bookkeeping connector removal and concurrency by removing
     * entries that have become too old.
     * <p>
     * <em>It is important to run this call for each transmission from the client</em>
     * , otherwise the bookkeeping gets out of date and the results form
     * {@link #connectorWasPresentAsRequestWasSent(String, long)} will become
     * invalid (that is, even though the client knew the component was removed,
     * the aforementioned method would start claiming otherwise).
     * <p>
     * Entries that both client and server agree upon are removed. Since
     * argument is the last sync id that the client has seen from the server, we
     * know that entries earlier than that cannot cause any problems anymore.
     * <p>
     * The sync id value <code>-1</code> is ignored to facilitate testing with
     * pre-recorded requests.
     *
     * @see #connectorWasPresentAsRequestWasSent(String, long)
     * @since 7.2
     * @param lastSyncIdSeenByClient
     *            the sync id the client has most recently received from the
     *            server.
     */
    public void cleanConcurrentlyRemovedConnectorIds(int lastSyncIdSeenByClient) {
        if (lastSyncIdSeenByClient == -1) {
            // Sync id checking is not in use, so we should just clear the
            // entire map to avoid leaking memory
            syncIdToUnregisteredConnectorIds.clear();
            return;
        }
        /*
         * We remove all entries _older_ than the one reported right now,
         * because the remaining still contain components that might cause
         * conflicts. In any case, it's better to clean up too little than too
         * much, especially as the data will hardly grow into the kilobytes.
         */
        syncIdToUnregisteredConnectorIds.headMap(lastSyncIdSeenByClient)
                .clear();
    }
}
