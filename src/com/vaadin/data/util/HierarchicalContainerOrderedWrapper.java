/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Container.Hierarchical;

/**
 * A wrapper class for adding external ordering to containers not implementing
 * the {@link com.vaadin.data.Container.Ordered} interface while retaining
 * {@link Hierarchical} features.
 * 
 * @see ContainerOrderedWrapper
 */
@SuppressWarnings({ "serial" })
public class HierarchicalContainerOrderedWrapper extends
        ContainerOrderedWrapper implements Hierarchical {

    private Hierarchical hierarchical;

    public HierarchicalContainerOrderedWrapper(Hierarchical toBeWrapped) {
        super(toBeWrapped);
        hierarchical = toBeWrapped;
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return hierarchical.areChildrenAllowed(itemId);
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        return hierarchical.getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return hierarchical.getParent(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return hierarchical.hasChildren(itemId);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return hierarchical.isRoot(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return hierarchical.rootItemIds();
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {
        return hierarchical.setChildrenAllowed(itemId, areChildrenAllowed);
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        return hierarchical.setParent(itemId, newParentId);
    }

}
