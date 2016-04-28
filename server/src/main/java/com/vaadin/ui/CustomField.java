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
        HasComponents {

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

        // If the content has not yet been created, create and attach it at
        // this point by calling getContent()
        getContent();
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
    public void setWidth(float width, Unit unit) {
        super.setWidth(width, unit);
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
    public Iterator<Component> iterator() {
        return new ComponentIterator();
    }
}
