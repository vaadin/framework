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

package com.vaadin.tokka.ui.components.fields;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

import com.vaadin.tokka.event.Registration;
import com.vaadin.tokka.ui.components.HasValue;
import com.vaadin.ui.AbstractComponent;

public abstract class AbstractField<T> extends AbstractComponent
        implements HasValue<T> {

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    public <L extends Consumer<T> & Serializable> Registration onValueChange(
            L listener) {
        return addValueChangeListener(e -> listener.accept(e.getValue()));
    }

    /**
     * Sets the value of this field.
     * 
     * @param value
     *            the new value to set
     * @param userOriginated
     *            whether the value change originates from the user
     */
    protected <E extends ValueChange<T>> void setValue(T value,
            boolean userOriginated) {
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
     * Stores the given field value to the shared state.
     * 
     * @param value
     *            the new value of the field
     */
    protected abstract void doSetValue(T value);

    /**
     * Returns a new value change event instance.
     * 
     * @param userOriginated
     *            whether the value change originates from the user
     * @return the new event
     */
    protected abstract ValueChange<T> createValueChange(boolean userOriginated);
}
