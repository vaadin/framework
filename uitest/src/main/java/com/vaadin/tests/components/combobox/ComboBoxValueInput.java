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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxValueInput extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        (getLayout()).setSpacing(true);

        ComboBox cb = getComboBox("A combobox", false, "default");
        addComponent(cb);

        cb = getComboBox("A combobox with input prompt", false,
                "default-prompt");
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A combobox with null item", true, "null");
        addComponent(cb);

        cb = getComboBox("A combobox with null item and input prompt", true,
                "null-prompt");
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A combobox with filteringMode off", false,
                "filtering-off");
        cb.setFilteringMode(FilteringMode.OFF);
        addComponent(cb);

    }

    @Override
    protected String getTestDescription() {
        return "A combobox should always show the selected value when it is not focused. Entering a text when nothing is selected and blurring the combobox should reset the value. The same should happen when a value is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3268;
    }

    private ComboBox getComboBox(String caption, boolean addNullItem,
            String id) {
        ComboBox cb = new ComboBox(caption);
        cb.setImmediate(true);
        if (addNullItem) {
            cb.addItem("Null item");
            cb.setNullSelectionItemId("Null item");
        }
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");

        cb.setId(id);

        return cb;
    }

}
