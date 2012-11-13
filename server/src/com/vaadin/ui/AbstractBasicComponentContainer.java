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

import com.vaadin.ui.HasComponents.ComponentAttachDetachNotifier;

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
        implements BasicComponentContainer, ComponentAttachDetachNotifier {

    /**
     * Gets the number of children this {@link ComponentContainer} has. This
     * must be symmetric with what {@link #iterator()} returns.
     * 
     * @return The number of child components this container has.
     */
    public abstract int getComponentCount();

    /* documented in interface */
    @Override
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);

    }

    /* documented in interface */
    @Override
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /* documented in interface */
    @Override
    public void addComponentDetachListener(ComponentDetachListener listener) {
        addListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /* documented in interface */
    @Override
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        removeListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
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

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }

        super.setVisible(visible);
        // If the visibility state is toggled it might affect all children
        // aswell, e.g. make container visible should make children visible if
        // they were only hidden because the container was hidden.
        markAsDirtyRecursive();
    }

}
