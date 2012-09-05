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
import java.lang.reflect.Method;
import java.util.Iterator;

import com.vaadin.data.Property;

/**
 * A {@link Field} whose UI content can be constructed by the user, enabling the
 * creation of e.g. form fields by composing Vaadin components. Customization of
 * both the visual presentation and the logic of the field is possible.
 * 
 * Subclasses must implement {@link #getType()} and {@link #initContent()}.
 * 
 * Most custom fields can simply compose a user interface that calls the methods
 * {@link #setInternalValue(Object)} and {@link #getInternalValue()} when
 * necessary.
 * 
 * It is also possible to override {@link #validate()},
 * {@link #setInternalValue(Object)}, {@link #commit()},
 * {@link #setPropertyDataSource(Property)}, {@link #isEmpty()} and other logic
 * of the field. Methods overriding {@link #setInternalValue(Object)} should
 * also call the corresponding superclass method.
 * 
 * @param <T>
 *            field value type
 * 
 * @since 7.0
 */
public abstract class CustomField<T> extends AbstractField<T> implements
        ComponentContainer {

    /**
     * The root component implementing the custom component.
     */
    private Component root = null;

    /**
     * Constructs a new custom field.
     * 
     * <p>
     * The component is implemented by wrapping the methods of the composition
     * root component given as parameter. The composition root must be set
     * before the component can be used.
     * </p>
     */
    public CustomField() {
        // expand horizontally by default
        setWidth(100, Unit.PERCENTAGE);
    }

    /**
     * Constructs the content and notifies it that the {@link CustomField} is
     * attached to a window.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        // First call super attach to notify all children (none if content has
        // not yet been created)
        super.attach();

        // If the content has not yet been created, we create and attach it at
        // this point.
        if (root == null) {
            // Ensure content is created and its parent is set.
            // The getContent() call creates the content and attaches the
            // content
            fireComponentAttachEvent(getContent());
        }
    }

    /**
     * Returns the content (UI) of the custom component.
     * 
     * @return Component
     */
    protected Component getContent() {
        if (null == root) {
            root = initContent();
            root.setParent(this);
        }
        return root;
    }

    /**
     * Create the content component or layout for the field. Subclasses of
     * {@link CustomField} should implement this method.
     * 
     * Note that this method is called when the CustomField is attached to a
     * layout or when {@link #getContent()} is called explicitly for the first
     * time. It is only called once for a {@link CustomField}.
     * 
     * @return {@link Component} representing the UI of the CustomField
     */
    protected abstract Component initContent();

    // Size related methods
    // TODO might not be necessary to override but following the pattern from
    // AbstractComponentContainer

    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
        markAsDirtyRecursive();
    }

    @Override
    public void setWidth(float height, Unit unit) {
        super.setWidth(height, unit);
        markAsDirtyRecursive();
    }

    // ComponentContainer methods

    private class ComponentIterator implements Iterator<Component>,
            Serializable {
        boolean first = (root != null);

        @Override
        public boolean hasNext() {
            return first;
        }

        @Override
        public Component next() {
            first = false;
            return getContent();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return new ComponentIterator();
    }

    @Override
    public Iterator<Component> iterator() {
        return getComponentIterator();
    }

    @Override
    public int getComponentCount() {
        return (null != getContent()) ? 1 : 0;
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

    // TODO remove these methods when ComponentContainer interface is cleaned up

    @Override
    public void addComponent(Component c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeComponent(Component c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        throw new UnsupportedOperationException();
    }

    private static final Method COMPONENT_ATTACHED_METHOD;

    static {
        try {
            COMPONENT_ATTACHED_METHOD = ComponentAttachListener.class
                    .getDeclaredMethod("componentAttachedToContainer",
                            new Class[] { ComponentAttachEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in CustomField");
        }
    }

    @Override
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addComponentAttachListener(com.vaadin.ui.ComponentContainer.ComponentAttachListener)}
     **/
    @Override
    @Deprecated
    public void addListener(ComponentAttachListener listener) {
        addComponentAttachListener(listener);
    }

    @Override
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeComponentAttachListener(com.vaadin.ui.ComponentContainer.ComponentAttachListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(ComponentAttachListener listener) {
        removeComponentAttachListener(listener);
    }

    @Override
    public void addComponentDetachListener(ComponentDetachListener listener) {
        // content never detached
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addComponentDetachListener(com.vaadin.ui.ComponentContainer.ComponentDetachListener)}
     **/
    @Override
    @Deprecated
    public void addListener(ComponentDetachListener listener) {
        addComponentDetachListener(listener);

    }

    @Override
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        // content never detached
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeComponentDetachListener(com.vaadin.ui.ComponentContainer.ComponentDetachListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(ComponentDetachListener listener) {
        removeComponentDetachListener(listener);
    }

    @Override
    public boolean isComponentVisible(Component childComponent) {
        return true;
    }
}
