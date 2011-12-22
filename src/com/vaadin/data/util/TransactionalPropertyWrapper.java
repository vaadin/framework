/* 
@VaadinApache2LicenseForJavaFiles@
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
 * @version @version@
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

    public TransactionalPropertyWrapper(Property<T> wrappedProperty) {
        this.wrappedProperty = wrappedProperty;
        if (wrappedProperty instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) wrappedProperty)
                    .addListener(new ValueChangeListener() {

                        public void valueChange(ValueChangeEvent event) {
                            fireValueChange();
                        }
                    });
        }
    }

    public Class getType() {
        return wrappedProperty.getType();
    }

    public T getValue() {
        return wrappedProperty.getValue();
    }

    public void setValue(Object newValue) throws ReadOnlyException {
        // Causes a value change to be sent to this listener which in turn fires
        // a new value change event for this property
        wrappedProperty.setValue(newValue);
    }

    public void startTransaction() {
        inTransaction = true;
        valueBeforeTransaction = getValue();
    }

    public void commit() {
        endTransaction();
    }

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

}
