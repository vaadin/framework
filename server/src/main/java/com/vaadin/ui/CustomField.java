/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.Collections;
import java.util.Iterator;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.customfield.CustomFieldState;

/**
 * A {@link HasValue} whose UI content can be constructed by the user, enabling
 * the creation of e.g. form fields by composing Vaadin components.
 * Customization of both the visual presentation and the logic of the field is
 * possible.
 * <p>
 * Subclasses must implement {@link #initContent()}.
 * <p>
 * Most custom fields can simply compose a user interface that calls the methods
 * {@link #doSetValue(Object)} and {@link #getValue()} when necessary.
 *
 * @param <T>
 *            field value type
 *
 * @since 8.0
 */
public abstract class CustomField<T> extends AbstractField<T>
        implements HasComponents {

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

    @Override
    protected CustomFieldState getState() {
        return (CustomFieldState) super.getState();
    }

    @Override
    protected CustomFieldState getState(boolean markAsDirty) {
        return (CustomFieldState) super.getState(markAsDirty);
    }

    // ComponentContainer methods

    @Override
    public Iterator<Component> iterator() {
        // Can't use getContent() here as this will cause an infinite loop if
        // initContent happens to all iterator(). This happens if you do
        // setWidth...
        if (root != null) {
            return Collections.singletonList(root).iterator();
        } else {
            return Collections.<Component> emptyList().iterator();
        }
    }

    /**
     * Sets the component to which all methods from the {@link Focusable}
     * interface should be delegated.
     * <p>
     * Set this to a wrapped field to include that field in the tabbing order,
     * to make it receive focus when {@link #focus()} is called and to make it
     * be correctly focused when used as a Grid editor component.
     * <p>
     * By default, {@link Focusable} events are handled by the super class and
     * ultimately ignored.
     *
     * @param focusDelegate
     *            the focusable component to which focus events are redirected
     */
    public void setFocusDelegate(Focusable focusDelegate) {
        getState().focusDelegate = focusDelegate;
    }

    private Focusable getFocusable() {
        return (Focusable) getState(false).focusDelegate;
    }

    @Override
    public void focus() {
        if (getFocusable() != null) {
            getFocusable().focus();
        } else {
            super.focus();
        }
    }

    @Override
    public int getTabIndex() {
        if (getFocusable() != null) {
            return getFocusable().getTabIndex();
        } else {
            return super.getTabIndex();
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        if (getFocusable() != null) {
            getFocusable().setTabIndex(tabIndex);
        } else {
            super.setTabIndex(tabIndex);
        }
    }

}
