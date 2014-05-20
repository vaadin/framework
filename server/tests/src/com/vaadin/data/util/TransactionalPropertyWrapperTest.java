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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.TextField;

/**
 * Test verifying that TransactionalPropertyWrapper removes it's listener from
 * wrapped Property
 * 
 * @since 7.1.15
 * @author Vaadin Ltd
 */
public class TransactionalPropertyWrapperTest {

    @SuppressWarnings("serial")
    public class TestingProperty<T extends Object> extends
            ObjectProperty<Object> {

        private List<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();

        public TestingProperty(Object value) {
            super(value);
        }

        @Override
        public void addValueChangeListener(ValueChangeListener listener) {
            super.addValueChangeListener(listener);
            listeners.add(listener);
        }

        @Override
        public void removeValueChangeListener(ValueChangeListener listener) {
            super.removeValueChangeListener(listener);
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }

        public boolean hasListeners() {
            return !listeners.isEmpty();
        }
    }

    private final TextField nameField = new TextField("Name");
    private final TextField ageField = new TextField("Age");
    private final TextField unboundField = new TextField("No FieldGroup");
    private final TestingProperty<String> unboundProp = new TestingProperty<String>(
            "Hello World");
    private final PropertysetItem item = new PropertysetItem();

    @Test
    public void fieldGroupBindAndUnbind() {
        item.addItemProperty("name", new TestingProperty<String>(
                "Just some text"));
        item.addItemProperty("age", new TestingProperty<String>("42"));

        final FieldGroup binder = new FieldGroup(item);
        binder.setBuffered(false);

        for (int i = 0; i < 2; ++i) {
            binder.bind(nameField, "name");
            binder.bind(ageField, "age");
            unboundField.setPropertyDataSource(unboundProp);

            assertTrue("No listeners in Properties", fieldsHaveListeners(true));

            binder.unbind(nameField);
            binder.unbind(ageField);
            unboundField.setPropertyDataSource(null);

            assertTrue("Listeners in Properties after unbinding",
                    fieldsHaveListeners(false));
        }
    }

    /**
     * Check that all listeners have same hasListeners() response
     * 
     * @param expected
     *            expected response
     * @return true if all are the same as expected. false if not
     */
    private boolean fieldsHaveListeners(boolean expected) {
        for (Object id : item.getItemPropertyIds()) {
            TestingProperty<?> itemProperty = (TestingProperty<?>) item
                    .getItemProperty(id);

            if (itemProperty.hasListeners() != expected) {
                return false;
            }
        }
        return unboundProp.hasListeners() == expected;
    }
}
