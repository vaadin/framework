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
 * Custom component provides simple implementation of Component interface for
 * creation of new UI components by composition of existing components.
 * <p>
 * The component is used by inheriting the CustomComponent class and setting
 * composite root inside the Custom component. The composite root itself can
 * contain more components, but their interfaces are hidden from the users.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CustomComponent extends AbstractComponentContainer {

    /**
     * The root component implementing the custom component.
     */
    private Component root = null;

    /**
     * Constructs a new custom component.
     * 
     * <p>
     * The component is implemented by wrapping the methods of the composition
     * root component given as parameter. The composition root must be set
     * before the component can be used.
     * </p>
     */
    public CustomComponent() {
        // expand horizontally by default
        setWidth(100, UNITS_PERCENTAGE);
    }

    /**
     * Constructs a new custom component.
     * 
     * <p>
     * The component is implemented by wrapping the methods of the composition
     * root component given as parameter. The composition root must not be null
     * and can not be changed after the composition.
     * </p>
     * 
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    public CustomComponent(Component compositionRoot) {
        this();
        setCompositionRoot(compositionRoot);
    }

    /**
     * Returns the composition root.
     * 
     * @return the Component Composition root.
     */
    protected Component getCompositionRoot() {
        return root;
    }

    /**
     * Sets the compositions root.
     * <p>
     * The composition root must be set to non-null value before the component
     * can be used. The composition root can only be set once.
     * </p>
     * 
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    protected void setCompositionRoot(Component compositionRoot) {
        if (compositionRoot != root) {
            if (root != null) {
                // remove old component
                super.removeComponent(root);
            }
            if (compositionRoot != null) {
                // set new component
                super.addComponent(compositionRoot);
            }
            root = compositionRoot;
            markAsDirty();
        }
    }

    /* Basic component features ------------------------------------------ */

    private class ComponentIterator implements Iterator<Component>,
            Serializable {
        boolean first = getCompositionRoot() != null;

        @Override
        public boolean hasNext() {
            return first;
        }

        @Override
        public Component next() {
            first = false;
            return root;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return new ComponentIterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components (zero or one)
     */
    @Override
    public int getComponentCount() {
        return (root != null ? 1 : 0);
    }

    /**
     * This method is not supported by CustomComponent.
     * 
     * @see com.vaadin.ui.ComponentContainer#replaceComponent(com.vaadin.ui.Component,
     *      com.vaadin.ui.Component)
     */
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by CustomComponent. Use
     * {@link CustomComponent#setCompositionRoot(Component)} to set
     * CustomComponents "child".
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#addComponent(com.vaadin.ui.Component)
     */
    @Override
    public void addComponent(Component c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by CustomComponent.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#moveComponentsFrom(com.vaadin.ui.ComponentContainer)
     */
    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by CustomComponent.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#removeAllComponents()
     */
    @Override
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by CustomComponent.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#removeComponent(com.vaadin.ui.Component)
     */
    @Override
    public void removeComponent(Component c) {
        throw new UnsupportedOperationException();
    }

}
