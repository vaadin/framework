package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.ui.AbstractField;

import org.junit.Test;

public class RemoveListenersOnDetach {

    int numValueChanges = 0;
    int numReadOnlyChanges = 0;

    AbstractField field = new AbstractField() {
        @Override
        public Class<?> getType() {
            return int.class;
        }

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            super.valueChange(event);
            numValueChanges++;
        }

        @Override
        public void readOnlyStatusChange(
                Property.ReadOnlyStatusChangeEvent event) {
            super.readOnlyStatusChange(event);
            numReadOnlyChanges++;
        }
    };

    Property property = new AbstractProperty() {
        private int value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object newValue) throws ReadOnlyException,
                ConversionException {
            value = (Integer) newValue;
            fireValueChange();
        }

        public Class<?> getType() {
            return int.class;
        }
    };

    @Test
    public void testAttachDetach() {
        field.setPropertyDataSource(property);

        property.setValue(1);
        property.setReadOnly(true);
        assertEquals(1, field.getValue());
        assertEquals(1, numValueChanges);
        assertEquals(1, numReadOnlyChanges);

        field.attach();
        property.setValue(2);
        property.setReadOnly(false);
        assertEquals(2, field.getValue());
        assertEquals(2, numValueChanges);
        assertEquals(2, numReadOnlyChanges);

        field.detach();
        property.setValue(3);
        property.setReadOnly(true);
        assertEquals(3, field.getValue());
        assertEquals(2, numValueChanges);
        assertEquals(2, numReadOnlyChanges);

        field.attach();
        assertEquals(3, field.getValue());
        assertEquals(3, numValueChanges);
    }
}
