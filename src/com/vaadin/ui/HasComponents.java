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

}
