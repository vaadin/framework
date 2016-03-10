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

import java.util.Iterator;

import com.vaadin.ui.HasComponents.ComponentAttachDetachNotifier;

/**
 * A special type of parent which allows the user to add and remove components
 * to it. Typically does not have any restrictions on the number of children it
 * can contain.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface ComponentContainer extends HasComponents,
        ComponentAttachDetachNotifier {

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(Component c);

    /**
     * Adds the components in the given order to this component container.
     * 
     * @param components
     *            The components to add.
     */
    public void addComponents(Component... components);

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    public void removeComponent(Component c);

    /**
     * Removes all components from this container.
     */
    public void removeAllComponents();

    /**
     * Replaces the component in the container with another one without changing
     * position.
     * 
     * <p>
     * This method replaces component with another one is such way that the new
     * component overtakes the position of the old component. If the old
     * component is not in the container, the new component is added to the
     * container. If the both component are already in the container, their
     * positions are swapped. Component attach and detach events should be taken
     * care as with add and remove.
     * </p>
     * 
     * @param oldComponent
     *            the old component that will be replaced.
     * @param newComponent
     *            the new component to be replaced.
     */
    public void replaceComponent(Component oldComponent, Component newComponent);

    /**
     * Gets an iterator to the collection of contained components. Using this
     * iterator it is possible to step through all components contained in this
     * container.
     * 
     * @return the component iterator.
     * 
     * @deprecated As of 7.0, use {@link #iterator()} instead.
     */
    @Deprecated
    public Iterator<Component> getComponentIterator();

    /**
     * Gets the number of children this {@link ComponentContainer} has. This
     * must be symmetric with what {@link #getComponentIterator()} returns.
     * 
     * @return The number of child components this container has.
     * @since 7.0.0
     */
    public int getComponentCount();

    /**
     * Moves all components from an another container into this container. The
     * components are removed from <code>source</code>.
     * 
     * @param source
     *            the container which contains the components that are to be
     *            moved to this container.
     */
    public void moveComponentsFrom(ComponentContainer source);

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addComponentAttachListener(ComponentAttachListener)}
     **/
    @Deprecated
    public void addListener(ComponentAttachListener listener);

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeComponentAttachListener(ComponentAttachListener)}
     **/
    @Deprecated
    public void removeListener(ComponentAttachListener listener);

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addComponentDetachListener(ComponentDetachListener)}
     **/
    @Deprecated
    public void addListener(ComponentDetachListener listener);

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeComponentDetachListener(ComponentDetachListener)}
     **/
    @Deprecated
    public void removeListener(ComponentDetachListener listener);

}
