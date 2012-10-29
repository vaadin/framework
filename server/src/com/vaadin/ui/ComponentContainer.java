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

import java.io.Serializable;
import java.util.Iterator;

/**
 * Extension to the {@link Component} interface which adds to it the capacity to
 * contain other components. All UI elements that can have child elements
 * implement this interface.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface ComponentContainer extends HasComponents {

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(Component c);

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
     * Listens the component attach events.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addComponentAttachListener(ComponentAttachListener listener);

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addComponentAttachListener(ComponentAttachListener)}
     **/
    @Deprecated
    public void addListener(ComponentAttachListener listener);

    /**
     * Stops the listening component attach events.
     * 
     * @param listener
     *            the listener to removed.
     */
    public void removeComponentAttachListener(ComponentAttachListener listener);

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeComponentAttachListener(ComponentAttachListener)}
     **/
    @Deprecated
    public void removeListener(ComponentAttachListener listener);

    /**
     * Listens the component detach events.
     */
    public void addComponentDetachListener(ComponentDetachListener listener);

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addComponentDetachListener(ComponentDetachListener)}
     **/
    @Deprecated
    public void addListener(ComponentDetachListener listener);

    /**
     * Stops the listening component detach events.
     */
    public void removeComponentDetachListener(ComponentDetachListener listener);

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeComponentDetachListener(ComponentDetachListener)}
     **/
    @Deprecated
    public void removeListener(ComponentDetachListener listener);

    /**
     * Component attach listener interface.
     */
    public interface ComponentAttachListener extends Serializable {

        /**
         * A new component is attached to container.
         * 
         * @param event
         *            the component attach event.
         */
        public void componentAttachedToContainer(ComponentAttachEvent event);
    }

    /**
     * Component detach listener interface.
     */
    public interface ComponentDetachListener extends Serializable {

        /**
         * A component has been detached from container.
         * 
         * @param event
         *            the component detach event.
         */
        public void componentDetachedFromContainer(ComponentDetachEvent event);
    }

    /**
     * Component attach event sent when a component is attached to container.
     */
    @SuppressWarnings("serial")
    public static class ComponentAttachEvent extends Component.Event {

        private final Component component;

        /**
         * Creates a new attach event.
         * 
         * @param container
         *            the component container the component has been detached
         *            to.
         * @param attachedComponent
         *            the component that has been attached.
         */
        public ComponentAttachEvent(ComponentContainer container,
                Component attachedComponent) {
            super(container);
            component = attachedComponent;
        }

        /**
         * Gets the component container.
         * 
         * @param the
         *            component container.
         */
        public ComponentContainer getContainer() {
            return (ComponentContainer) getSource();
        }

        /**
         * Gets the attached component.
         * 
         * @param the
         *            attach component.
         */
        public Component getAttachedComponent() {
            return component;
        }
    }

    /**
     * Component detach event sent when a component is detached from container.
     */
    @SuppressWarnings("serial")
    public static class ComponentDetachEvent extends Component.Event {

        private final Component component;

        /**
         * Creates a new detach event.
         * 
         * @param container
         *            the component container the component has been detached
         *            from.
         * @param detachedComponent
         *            the component that has been detached.
         */
        public ComponentDetachEvent(ComponentContainer container,
                Component detachedComponent) {
            super(container);
            component = detachedComponent;
        }

        /**
         * Gets the component container.
         * 
         * @param the
         *            component container.
         */
        public ComponentContainer getContainer() {
            return (ComponentContainer) getSource();
        }

        /**
         * Gets the detached component.
         * 
         * @return the detached component.
         */
        public Component getDetachedComponent() {
            return component;
        }
    }

}
