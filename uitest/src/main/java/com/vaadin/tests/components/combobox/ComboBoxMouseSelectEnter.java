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
package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxMouseSelectEnter extends AbstractTestUI {
    protected ComboBox comboBox;

    @Override
    protected void setup(VaadinRequest request) {
        comboBox = new ComboBox();
        final Label label = new Label();
        label.setId("value");

        comboBox.setTextInputAllowed(true);
        comboBox.setNullSelectionAllowed(true);
        comboBox.setNullSelectionItemId(null);

        for (int i = 0; i < 10; i++) {
            comboBox.addItem("a" + i);
        }

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value != null) {
                    label.setValue(value.toString());
                } else {
                    label.setValue("null");
                }
            }
        });

        addComponents(comboBox);
        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "Pressing Enter should set value highlighted from mouse position after using arrow keys";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16981;
    }
}
