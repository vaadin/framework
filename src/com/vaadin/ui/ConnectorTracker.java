/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.terminal.AbstractClientConnector;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.server.ClientConnector;

/**
 * A class which takes care of book keeping of {@link ClientConnector}s for one
 * Root.
 * <p>
 * Provides {@link #getConnector(String)} which can be used to lookup a
 * connector from its id. This is for framework use only and should not be
 * needed in applications.
 * </p>
 * <p>
 * Tracks which {@link ClientConnector}s are dirty so they can be updated to the
 * client when the following response is sent. A connector is dirty when an
 * operation has been performed on it on the server and as a result of this
 * operation new information needs to be sent to its {@link ServerConnector}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class ConnectorTracker implements Serializable {

    private final HashMap<String, ClientConnector> connectorIdToConnector = new HashMap<String, ClientConnector>();
    private Set<ClientConnector> dirtyConnectors = new HashSet<ClientConnector>();

    private Root root;

    /**
     * Gets a logger for this class
     * 
     * @return A logger instance for logging within this class
     * 
     */
    public static Logger getLogger() {
        return Logger.getLogger(ConnectorTracker.class.getName());
    }

    public ConnectorTracker(Root root) {
        this.root = root;
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
        connectorIdToConnector.remove(connectorId);
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
            if (connector instanceof Component) {
                Component component = (Component) connector;
                if (component.getRoot() != root) {
                    // If component is no longer part of this application,
                    // remove it from the map. If it is re-attached to the
                    // application at some point it will be re-added through
                    // registerConnector(connector)
                    iterator.remove();
                }
            }
        }

    }

    public void markDirty(ClientConnector connector) {
        if (getLogger().isLoggable(Level.FINE)) {
            if (!dirtyConnectors.contains(connector)) {
                getLogger()
                        .fine(getDebugInfo(connector) + " " + "is now dirty");
            }
        }

        dirtyConnectors.add(connector);
    }

    public void markClean(ClientConnector connector) {
        if (getLogger().isLoggable(Level.FINE)) {
            if (dirtyConnectors.contains(connector)) {
                getLogger().fine(
                        getDebugInfo(connector) + " " + "is no longer dirty");
            }
        }

        dirtyConnectors.remove(connector);
    }

    private String getDebugInfo(ClientConnector connector) {
        String message = getObjectString(connector);
        if (connector.getParent() != null) {
            message += " (parent: " + getObjectString(connector.getParent())
                    + ")";
        }
        return message;
    }

    private String getObjectString(Object connector) {
        return connector.getClass().getName() + "@"
                + Integer.toHexString(connector.hashCode());
    }

    public void markAllConnectorsDirty() {
        markConnectorsDirtyRecursively(root);
        getLogger().fine("All connectors are now dirty");
    }

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

    public Collection<ClientConnector> getDirtyConnectors() {
        return dirtyConnectors;
    }

}
