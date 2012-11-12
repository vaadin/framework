/*
 * Copyright 2011-2012 Vaadin Ltd.
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

import java.lang.reflect.Method;

import com.vaadin.ui.ComponentContainer.ComponentAttachEvent;
import com.vaadin.ui.ComponentContainer.ComponentAttachListener;
import com.vaadin.ui.ComponentContainer.ComponentDetachEvent;
import com.vaadin.ui.ComponentContainer.ComponentDetachListener;

/**
 * Abstract base class for component containers that may have one or more child
 * components.
 * 
 * For most component containers that support multiple children, inherit
 * {@link AbstractComponentContainer} instead of this class.
 * 
 * @since 7.0
 */
public abstract class AbstractBasicComponentContainer extends AbstractComponent
        implements HasComponents {

    /* Events */

    private static final Method COMPONENT_ATTACHED_METHOD;

    private static final Method COMPONENT_DETACHED_METHOD;

    static {
        try {
            COMPONENT_ATTACHED_METHOD = ComponentAttachListener.class
                    .getDeclaredMethod("componentAttachedToContainer",
                            new Class[] { ComponentAttachEvent.class });
            COMPONENT_DETACHED_METHOD = ComponentDetachListener.class
                    .getDeclaredMethod("componentDetachedFromContainer",
                            new Class[] { ComponentDetachEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractComponentContainer");
        }
    }

    /**
     * Gets the number of children this {@link ComponentContainer} has. This
     * must be symmetric with what {@link #iterator()} returns.
     * 
     * @return The number of child components this container has.
     */
    public abstract int getComponentCount();

    /**
     * Listens the component attach events.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);

    }

    /**
     * Stops the listening component attach events.
     * 
     * @param listener
     *            the listener to removed.
     */
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);
    }

    /**
     * Listens the component detach events.
     */
    public void addComponentDetachListener(ComponentDetachListener listener) {
        addListener(ComponentContainer.ComponentDetachEvent.class, listener,
                COMPONENT_DETACHED_METHOD);
    }

    /**
     * Stops the listening component detach events.
     */
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        removeListener(ComponentContainer.ComponentDetachEvent.class, listener,
                COMPONENT_DETACHED_METHOD);
    }

    /**
     * Fires the component attached event. This should be called by the
     * addComponent methods after the component have been added to this
     * container.
     * 
     * @param component
     *            the component that has been added to this container.
     */
    protected void fireComponentAttachEvent(Component component) {
        fireEvent(new ComponentAttachEvent(this, component));
    }

    /**
     * Fires the component detached event. This should be called by the
     * removeComponent methods after the component have been removed from this
     * container.
     * 
     * @param component
     *            the component that has been removed from this container.
     */
    protected void fireComponentDetachEvent(Component component) {
        fireEvent(new ComponentDetachEvent(this, component));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.HasComponents#isComponentVisible(com.vaadin.ui.Component)
     */
    @Override
    public boolean isComponentVisible(Component childComponent) {
        return true;
    }

}
