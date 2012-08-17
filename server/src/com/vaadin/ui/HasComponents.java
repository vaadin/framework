/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.Iterator;

/**
 * Interface that must be implemented by all {@link Component}s that contain
 * other {@link Component}s.
 * 
 * @author Vaadin Ltd
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
     * 
     * @deprecated Use {@link #iterator()} instead.
     */
    @Deprecated
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

}
