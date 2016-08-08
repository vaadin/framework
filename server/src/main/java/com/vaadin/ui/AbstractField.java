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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.data.HasValue;
import com.vaadin.event.Registration;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.ReflectTools;

/**
 * An abstract implementation of a field, or a {@code Component} allowing user
 * input. Implements {@link HasValue} to represent the input value. Examples of
 * typical field components include text fields, date pickers, and check boxes.
 * <p>
 * This class replaces the Vaadin 7 {@code com.vaadin.ui.AbstractField} class.
 * The old {@code AbstractField} is retained, under the new name
 * {@link com.vaadin.legacy.ui.LegacyAbstractField}, for compatibility and
 * migration purposes.
 *
 * @author Vaadin Ltd.
 * @since
 *
 * @param <T>
 *            the input value type
 */
public abstract class AbstractField<T> extends AbstractComponent
        implements HasValue<T>, Focusable {

    @Deprecated
    private static final Method VALUE_CHANGE_METHOD = ReflectTools
            .findMethod(ValueChangeListener.class, "accept", ValueChange.class);

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    /**
     * Returns whether the value of this field can be changed by the user or
     * not. By default fields are not read-only.
     *
     * @return {@code true} if this field is in read-only mode, {@code false}
     *         otherwise.
     *
     * @see #setReadOnly(boolean)
     */
    @Override
    public boolean isReadOnly() {
        return super.isReadOnly();
    }

    /**
     * Sets whether the value of this field can be changed by the user or not. A
     * field in read-only mode typically looks visually different to signal to
     * the user that the value cannot be edited.
     * <p>
     * The server ignores (potentially forged) value change requests from the
     * client to fields that are read-only. Programmatically changing the field
     * value via {@link #setValue(T)} is still possible.
     * <p>
     * The read-only mode is distinct from the
     * {@linkplain Component#setEnabled(boolean) disabled} state. When disabled,
     * a component cannot be interacted with at all, and its content should be
     * considered irrelevant or not applicable. In contrast, the user should
     * still be able to read the content and otherwise interact with a read-only
     * field even though changing the value is disallowed.
     *
     * @param readOnly
     *            {@code true} to set read-only mode, {@code false} otherwise.
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<? super T> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(ValueChange.class, listener, VALUE_CHANGE_METHOD);
        return () -> removeListener(ValueChange.class, listener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (attr.hasKey("readonly")) {
            setReadOnly(DesignAttributeHandler.readAttribute("readonly", attr,
                    Boolean.class));
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        AbstractField<T> def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("readonly", attr,
                super.isReadOnly(), def.isReadOnly(), Boolean.class);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        attributes.add("readonly");
        // must be handled by subclasses
        attributes.add("value");
        return attributes;
    }

    /**
     * Sets the value of this field if it has changed and fires a value change
     * event. If the value originates from the client and this field is
     * read-only, does nothing. Invokes {@link #doSetValue(Object) doSetValue}
     * to actually store the value.
     *
     * @param value
     *            the new value to set
     * @return {@code true} if this event originates from the client,
     *         {@code false} otherwise.
     */
    protected void setValue(T value, boolean userOriginated) {
        if (userOriginated && isReadOnly()) {
            return;
        }
        if (Objects.equals(value, getValue())) {
            return;
        }
        doSetValue(value);
        if (!userOriginated) {
            markAsDirty();
        }
        fireEvent(createValueChange(userOriginated));
    }

    /**
     * Sets the value of this field. May do sanitization or throw
     * {@code IllegalArgumentException} if the value is invalid. Typically saves
     * the value to shared state.
     *
     * @param value
     *            the new value of the field
     * @throws IllegalArgumentException
     *             if the value is invalid
     */
    protected abstract void doSetValue(T value);

    /**
     * Returns a new value change event instance.
     *
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     * @return the new event
     */
    protected ValueChange<T> createValueChange(boolean userOriginated) {
        return new ValueChange<>(this, userOriginated);
    }

    @Override
    protected AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    protected AbstractFieldState getState(boolean markAsDirty) {
        return (AbstractFieldState) super.getState(markAsDirty);
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

}
