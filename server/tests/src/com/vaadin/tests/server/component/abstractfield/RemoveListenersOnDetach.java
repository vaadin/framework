package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.UI;

public class RemoveListenersOnDetach {

    int numValueChanges = 0;
    int numReadOnlyChanges = 0;

    AbstractField field = new AbstractField() {
        final private VaadinSession application = new VaadinSession() {

        };
        private UI uI = new UI() {

            @Override
            protected void init(WrappedRequest request) {

            }

            @Override
            public VaadinSession getSession() {
                return application;
            }

        };

        @Override
        public Class<?> getType() {
            return String.class;
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
        public com.vaadin.ui.UI getUI() {
            return uI;
        };

        @Override
        public VaadinSession getSession() {
            return application;
        };
    };

    Property property = new AbstractProperty() {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException,
                ConversionException {
            fireValueChange();
        }

        @Override
        public Class<?> getType() {
            return String.class;
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
