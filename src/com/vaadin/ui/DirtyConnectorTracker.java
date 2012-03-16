package com.vaadin.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.vaadin.terminal.Paintable.RepaintRequestEvent;
import com.vaadin.terminal.Paintable.RepaintRequestListener;

public class DirtyConnectorTracker implements RepaintRequestListener {
    private Set<Component> dirtyComponents = new HashSet<Component>();
    private Root root;

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
        // TODO Remove debug info
        if (!dirtyComponents.contains(component)) {
            debug(component, "is now dirty");

        }
        dirtyComponents.add(component);
    }

    private void debug(Component component, String string) {
        getLogger().info(getDebugInfo(component) + " " + string);
    }

    private void markClean(Component component) {
        // TODO Remove debug info
        if (dirtyComponents.contains(component)) {
            debug(component, "is no longer dirty");
        }
        dirtyComponents.remove(component);

        // TODO .... WTF ....
        component.requestRepaintRequests();

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
        System.out.println("All components are now dirty");

    }

    public void markAllComponentsClean() {
        dirtyComponents.clear();
        System.out.println("All components are now clean");
    }

    private void markComponentsDirtyRecursively(Component c) {
        markDirty(c);
        if (c instanceof HasComponents) {
            HasComponents container = (HasComponents) c;
            for (Component child : container) {
                markComponentsDirtyRecursively(child);
            }
        }

    }

    public Collection<Component> getDirtyComponents() {
        return dirtyComponents;
    }

}
