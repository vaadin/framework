/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.util.Iterator;

/**
 * Interface that must be implemented by all {@link Component}s that contain
 * other {@link Component}s.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface HasComponents extends Component, Iterable<Component> {
    /**
     * Gets an iterator to the collection of contained components. Using this
     * iterator it is possible to step through all components contained in this
     * container.
     * 
     * @return the component iterator.
     */
    public Iterator<Component> getComponentIterator();

    /**
     * Checks if the child component is visible. This method allows hiding a
     * child component from updates and communication to and from the client.
     * This is useful for components that show only a limited number of its
     * children at any given time and want to allow updates only for the
     * children that are visible (e.g. TabSheet has one tab open at a time).
     * <p>
     * Note that this will prevent updates from reaching the child even though
     * the child itself is set to visible. Also if a child is set to invisible
     * this will not force it to be visible.
     * </p>
     * 
     * @param childComponent
     *            The child component to check
     * @return true if the child component is visible to the user, false
     *         otherwise
     */
    public boolean isComponentVisible(Component childComponent);

    /**
     * Causes a repaint of this component, and all components below it.
     * 
     * This should only be used in special cases, e.g when the state of a
     * descendant depends on the state of a ancestor.
     */
    public void requestRepaintAll();

}
