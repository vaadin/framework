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
import com.vaadin.ui.TextField;

public class ComboBoxSelecting extends AbstractReindeerTestUI {
    protected ComboBox<String> comboBox;
    protected List<String> items = new ArrayList<>();

    @Override
    protected void setup(VaadinRequest request) {
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                items.add("" + c + i);
            }
        }
        comboBox = new ComboBox<>(null, items);
        final Label label = new Label();
        label.setId("value");

        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);

        comboBox.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value != null) {
                label.setValue(value);
            } else {
                label.setValue("null");
            }
        });

        // Had to add an extra text field for our old Firefox browsers, because
        // tab will otherwise send the focus to address bar and FF 24 won't fire
        // a key event properly. Nice!
        addComponents(comboBox, label, new TextField());
    }

    @Override
    protected String getTestDescription() {
        return "Clearing the filter and hitting enter should select the null item";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15502;
    }
}
