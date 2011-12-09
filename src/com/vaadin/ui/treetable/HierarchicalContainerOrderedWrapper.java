/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui.treetable;

import java.util.Collection;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.ContainerOrderedWrapper;

@SuppressWarnings({ "serial", "unchecked" })
/**
 * Helper for TreeTable. Does the same thing as ContainerOrderedWrapper 
 * to fit into table but retains Hierarchical feature.
 */
public class HierarchicalContainerOrderedWrapper extends
        ContainerOrderedWrapper implements Hierarchical {

    private Hierarchical hierarchical;

    public HierarchicalContainerOrderedWrapper(Hierarchical toBeWrapped) {
        super(toBeWrapped);
        hierarchical = toBeWrapped;
    }

    public boolean areChildrenAllowed(Object itemId) {
        return hierarchical.areChildrenAllowed(itemId);
    }

    public Collection<?> getChildren(Object itemId) {
        return hierarchical.getChildren(itemId);
    }

    public Object getParent(Object itemId) {
        return hierarchical.getParent(itemId);
    }

    public boolean hasChildren(Object itemId) {
        return hierarchical.hasChildren(itemId);
    }

    public boolean isRoot(Object itemId) {
        return hierarchical.isRoot(itemId);
    }

    public Collection<?> rootItemIds() {
        return hierarchical.rootItemIds();
    }

    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {
        return hierarchical.setChildrenAllowed(itemId, areChildrenAllowed);
    }

    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        return hierarchical.setParent(itemId, newParentId);
    }

}
