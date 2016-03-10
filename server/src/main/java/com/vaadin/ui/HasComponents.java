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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import com.vaadin.util.ReflectTools;

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
     */
    @Override
    public Iterator<Component> iterator();

    /**
     * Interface for {@link HasComponents} implementations that support sending
     * attach and detach events for components.
     * 
     * @since 7.0
     */
    public interface ComponentAttachDetachNotifier extends Serializable {
        /**
         * Listens the component attach events.
         * 
         * @param listener
         *            the listener to add.
         */
        public void addComponentAttachListener(ComponentAttachListener listener);

        /**
         * Stops the listening component attach events.
         * 
         * @param listener
         *            the listener to removed.
         */
        public void removeComponentAttachListener(
                ComponentAttachListener listener);

        /**
         * Listens the component detach events.
         */
        public void addComponentDetachListener(ComponentDetachListener listener);

        /**
         * Stops the listening component detach events.
         */
        public void removeComponentDetachListener(
                ComponentDetachListener listener);
    }

    /**
     * Component attach listener interface.
     */
    public interface ComponentAttachListener extends Serializable {

        public static final Method attachMethod = ReflectTools.findMethod(
                ComponentAttachListener.class, "componentAttachedToContainer",
                ComponentAttachEvent.class);

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

        public static final Method detachMethod = ReflectTools.findMethod(
                ComponentDetachListener.class,
                "componentDetachedFromContainer", ComponentDetachEvent.class);

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
         *            the container the component has been detached to.
         * @param attachedComponent
         *            the component that has been attached.
         */
        public ComponentAttachEvent(HasComponents container,
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
        public HasComponents getContainer() {
            return (HasComponents) getSource();
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
         *            the container the component has been detached from.
         * @param detachedComponent
         *            the component that has been detached.
         */
        public ComponentDetachEvent(HasComponents container,
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
        public HasComponents getContainer() {
            return (HasComponents) getSource();
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
