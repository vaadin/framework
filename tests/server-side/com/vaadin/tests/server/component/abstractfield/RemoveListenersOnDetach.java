package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Root;

public class RemoveListenersOnDetach {

    int numValueChanges = 0;
    int numReadOnlyChanges = 0;

    AbstractField field = new AbstractField() {
        private Root root = new Root() {

            @Override
            protected void init(WrappedRequest request) {

            }

        };

        @Override
        public Class<?> getType() {
            return null;
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

        @Override
        public com.vaadin.ui.Root getRoot() {
            return root;
        };
    };

    Property property = new AbstractProperty() {
        public Object getValue() {
            return null;
        }

        public void setValue(Object newValue) throws ReadOnlyException,
                ConversionException {
            fireValueChange();
        }

        public Class<?> getType() {
            return null;
        }
    };

    @Test
    public void testAttachDetach() {
        field.setPropertyDataSource(property);

        property.setValue(null);
        property.setReadOnly(true);
        assertEquals(1, numValueChanges);
        assertEquals(1, numReadOnlyChanges);

        field.attach();
        property.setValue(null);
        property.setReadOnly(false);
        assertEquals(2, numValueChanges);
        assertEquals(2, numReadOnlyChanges);

        field.detach();
        property.setValue(null);
        property.setReadOnly(true);
        assertEquals(2, numValueChanges);
        assertEquals(2, numReadOnlyChanges);
    }
}
