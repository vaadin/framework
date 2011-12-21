/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.TransactionalProperty;

public class TransactionalPropertyWrapper<T> extends AbstractProperty<T>
        implements ValueChangeNotifier, TransactionalProperty<T> {

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
