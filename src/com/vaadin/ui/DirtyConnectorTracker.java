package com.vaadin.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.terminal.Paintable.RepaintRequestEvent;
import com.vaadin.terminal.Paintable.RepaintRequestListener;
import com.vaadin.terminal.gwt.server.AbstractCommunicationManager;
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
public class DirtyConnectorTracker implements RepaintRequestListener {
    private Set<Component> dirtyComponents = new HashSet<Component>();
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

    public void repaintRequested(RepaintRequestEvent event) {
        markDirty((Component) event.getPaintable());
    }

    public void componentAttached(Component component) {
        component.addListener(this);
        markDirty(component);
    }

    private void markDirty(Component component) {
        if (getLogger().isLoggable(Level.FINE)) {
            if (!dirtyComponents.contains(component)) {
                getLogger()
                        .fine(getDebugInfo(component) + " " + "is now dirty");
            }
        }

        dirtyComponents.add(component);
    }

    private void markClean(Component component) {
        if (getLogger().isLoggable(Level.FINE)) {
            if (dirtyComponents.contains(component)) {
                getLogger().fine(
                        getDebugInfo(component) + " " + "is no longer dirty");
            }
        }

        dirtyComponents.remove(component);
    }

    private String getDebugInfo(Component component) {
        String message = getObjectString(component);
        if (component.getParent() != null) {
            message += " (parent: " + getObjectString(component.getParent())
                    + ")";
        }
        return message;
    }

    private String getObjectString(Object component) {
        return component.getClass().getName() + "@"
                + Integer.toHexString(component.hashCode());
    }

    public void componentDetached(Component component) {
        component.removeListener(this);
        markClean(component);
    }

    public void markAllComponentsDirty() {
        markComponentsDirtyRecursively(root);
        getLogger().fine("All components are now dirty");
    }

    public void markAllComponentsClean() {
        dirtyComponents.clear();
        getLogger().fine("All components are now clean");
    }

    /**
     * Marks all visible components dirty, starting from the given component and
     * going downwards in the hierarchy.
     * 
     * @param c
     *            The component to start iterating downwards from
     */
    private void markComponentsDirtyRecursively(Component c) {
        if (!c.isVisible()) {
            return;
        }
        markDirty(c);
        if (c instanceof HasComponents) {
            HasComponents container = (HasComponents) c;
            for (Component child : AbstractCommunicationManager
                    .getChildComponents(container)) {
                markComponentsDirtyRecursively(child);
            }
        }

    }

    public Collection<Component> getDirtyComponents() {
        return dirtyComponents;
    }

}
