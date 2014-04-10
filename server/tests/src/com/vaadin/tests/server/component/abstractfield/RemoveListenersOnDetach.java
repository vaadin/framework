package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.UI;

public class RemoveListenersOnDetach {

    int numValueChanges = 0;
    int numReadOnlyChanges = 0;

    AbstractField field = new AbstractField() {
        final private VaadinSession application = new AlwaysLockedVaadinSession(
                null);
        private UI uI = new UI() {

            @Override
            protected void init(VaadinRequest request) {

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
        }

        @Override
        public VaadinSession getSession() {
            return application;
        }
    };

    Property<String> property = new AbstractProperty<String>() {
        @Override
        public String getValue() {
            return null;
        }

        @Override
        public void setValue(String newValue) throws ReadOnlyException,
                ConversionException {
            fireValueChange();
        }

        @Override
        public Class<String> getType() {
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
