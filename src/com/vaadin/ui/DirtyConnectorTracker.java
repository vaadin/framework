/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.terminal.AbstractClientConnector;
import com.vaadin.terminal.gwt.server.ClientConnector;

/**
 * A class that tracks dirty {@link ClientConnector}s. A {@link ClientConnector}
 * is dirty when an operation has been performed on it on the server and as a
 * result of this operation new information needs to be sent to its client side
 * counterpart.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class DirtyConnectorTracker implements Serializable {
    private Set<ClientConnector> dirtyConnectors = new HashSet<ClientConnector>();
    private Root root;

    /**
     * Gets a logger for this class
     * 
     * @return A logger instance for logging within this class
     * 
     */
    public static Logger getLogger() {
        return Logger.getLogger(DirtyConnectorTracker.class.getName());
    }

    public DirtyConnectorTracker(Root root) {
        this.root = root;
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
                .getAllChildrenIteratable(c)) {
            markConnectorsDirtyRecursively(child);
        }
    }

    public Collection<ClientConnector> getDirtyConnectors() {
        return dirtyConnectors;
    }

}
