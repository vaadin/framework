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
package com.vaadin.test.webcomponent.addon;

import java.util.Objects;

import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractJavaScriptComponent;

public abstract class AbstractJavaScriptField<T>
        extends AbstractJavaScriptComponent implements HasValue<T> {

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    /**
     * Sets the value of this field if it has changed and fires a value change
     * event. If the value originates from the client and this field is
     * read-only, does nothing. Invokes {@link #doSetValue(Object) doSetValue}
     * to actually store the value.
     *
     * @param value
     *            the new value to set
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     * @return <code>true</code> if the value was updated, <code>false</code>
     *         otherwise
     */
    protected boolean setValue(T value, boolean userOriginated) {
        if (userOriginated && isReadOnly()) {
            return false;
        }
        if (!isDifferentValue(value)) {
            return false;
        }
        T oldValue = getValue();
        doSetValue(value);
        if (!userOriginated) {
            markAsDirty();
        }
        fireEvent(createValueChange(oldValue, userOriginated));

        return true;
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
     * Called when a new value is set to determine whether the provided new
     * value is considered to be a change compared to the current value. This is
     * used to determine whether {@link #doSetValue(Object)} should be called
     * and a value change event fired.
     *
     * @param newValue
     *            the new value candidate to check, may be <code>null</code>
     *
     * @return <code>true</code> if the provided value is considered to be
     *         different and a value change event should be fired;
     *         <code>false</code> if the values are considered to be the same
     *         and no value change should be fired
     */
    protected boolean isDifferentValue(T newValue) {
        return !Objects.equals(newValue, getValue());
    }

    /**
     * Returns a new value change event instance.
     *
     * @param oldValue
     *            the value of this field before this value change event
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     * @return the new event
     */
    protected ValueChangeEvent<T> createValueChange(T oldValue,
            boolean userOriginated) {
        return new ValueChangeEvent<T>(this, oldValue, userOriginated);
    }

    @Override
    public Registration addValueChangeListener(
            com.vaadin.data.HasValue.ValueChangeListener<T> listener) {
        return addListener(ValueChangeEvent.class, listener,
                ValueChangeListener.VALUE_CHANGE_METHOD);
    }

    @Override
    public abstract boolean isReadOnly();

    @Override
    public abstract void setReadOnly(boolean readOnly);

    @Override
    public abstract boolean isRequiredIndicatorVisible();

    @Override
    public abstract void setRequiredIndicatorVisible(boolean visible);

}
