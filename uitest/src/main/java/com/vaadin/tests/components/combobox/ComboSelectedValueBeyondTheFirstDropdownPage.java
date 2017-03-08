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
package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPage
        extends AbstractReindeerTestUI {

    protected static final int ITEM_COUNT = 21;
    protected static final String ITEM_NAME_TEMPLATE = "Item %d";

    @Override
    protected void setup(VaadinRequest request) {
        Label value = getLabel();
        ComboBox<String> combobox = getComboBox(value);

        addComponent(combobox);
        addComponent(value);
    }

    private Label getLabel() {
        final Label value = new Label();
        value.setId("value");

        return value;
    }

    private ComboBox<String> getComboBox(final Label value) {
        final ComboBox<String> combobox = new ComboBox<>("MyCaption");
        combobox.setDescription(
                "ComboBox with more than 10 elements in it's dropdown list.");

        List<String> items = new ArrayList<>();
        for (int i = 1; i <= ITEM_COUNT; i++) {
            items.add(String.format(ITEM_NAME_TEMPLATE, i));
        }
        combobox.setItems(items);

        combobox.addValueChangeListener(
                event -> value.setValue(String.valueOf(event.getValue())));

        return combobox;
    }

    @Override
    protected String getTestDescription() {
        return "Test for ensuring that ComboBox shows selected value beyound the first dropdown page";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10600;
    }
}
