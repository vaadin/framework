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
package com.vaadin.data.util;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * Wrapper class that helps implement two-phase commit for a non-transactional
 * property.
 * 
 * When accessing the property through the wrapper, getting and setting the
 * property value take place immediately. However, the wrapper keeps track of
 * the old value of the property so that it can be set for the property in case
 * of a roll-back. This can result in the underlying property value changing
 * multiple times (first based on modifications made by the application, then
 * back upon roll-back).
 * 
 * Value change events on the {@link TransactionalPropertyWrapper} are only
 * fired at the end of a successful transaction, whereas listeners attached to
 * the underlying property may receive multiple value change events.
 * 
 * @see com.vaadin.data.Property.Transactional
 * 
 * @author Vaadin Ltd
 * @since 7.0
 * 
 * @param <T>
 */
public class TransactionalPropertyWrapper<T> extends AbstractProperty<T>
        implements ValueChangeNotifier, Property.Transactional<T> {

    private Property<T> wrappedProperty;
    private boolean inTransaction = false;
    private boolean valueChangePending;
    private T valueBeforeTransaction;
    private final ValueChangeListener listener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            fireValueChange();
        }
    };

    public TransactionalPropertyWrapper(Property<T> wrappedProperty) {
        this.wrappedProperty = wrappedProperty;
        if (wrappedProperty instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) wrappedProperty)
                    .addValueChangeListener(listener);
        }
    }

    /**
     * Removes the ValueChangeListener from wrapped Property that was added by
     * TransactionalPropertyWrapper.
     * 
     * @since 7.1.15
     */
    public void detachFromProperty() {
        if (wrappedProperty instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) wrappedProperty)
                    .removeValueChangeListener(listener);
        }
    }

    @Override
    public Class getType() {
        return wrappedProperty.getType();
    }

    @Override
    public T getValue() {
        return wrappedProperty.getValue();
    }

    @Override
    public void setValue(T newValue) throws ReadOnlyException {
        // Causes a value change to be sent to this listener which in turn fires
        // a new value change event for this property
        wrappedProperty.setValue(newValue);
    }

    @Override
    public void startTransaction() {
        inTransaction = true;
        valueBeforeTransaction = getValue();
    }

    @Override
    public void commit() {
        endTransaction();
    }

    @Override
    public void rollback() {
        try {
            wrappedProperty.setValue(valueBeforeTransaction);
        } finally {
            valueChangePending = false;
            endTransaction();
        }
    }

    protected void endTransaction() {
        inTransaction = false;
        valueBeforeTransaction = null;
        if (valueChangePending) {
            fireValueChange();
        }
    }

    @Override
    protected void fireValueChange() {
        if (inTransaction) {
            valueChangePending = true;
        } else {
            super.fireValueChange();
        }
    }

    public Property<T> getWrappedProperty() {
        return wrappedProperty;
    }

    @Override
    public boolean isReadOnly() {
        return wrappedProperty.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        boolean oldStatus = isReadOnly();
        wrappedProperty.setReadOnly(newStatus);
        if (oldStatus != isReadOnly()) {
            fireReadOnlyStatusChange();
        }
    }

}
