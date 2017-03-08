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
package com.vaadin.v7.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.AbstractProperty;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.AbstractField;

public class RemoveListenersOnDetachTest {

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
        public void setValue(String newValue)
                throws ReadOnlyException, ConversionException {
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
