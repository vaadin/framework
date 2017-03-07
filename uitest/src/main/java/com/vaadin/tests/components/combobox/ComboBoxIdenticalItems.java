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
import com.vaadin.tests.util.Log;
import com.vaadin.ui.ComboBox;

public class ComboBoxIdenticalItems extends AbstractReindeerTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> select = new ComboBox<>("ComboBox");
        select.setItemCaptionGenerator(
                item -> item.startsWith("one") ? "One" : "Two");
        select.setItems("one-1", "one-2", "two");
        select.setEmptySelectionAllowed(false);
        select.addValueChangeListener(
                event -> log.log("Item " + select.getValue() + " selected"));

        addComponent(log);
        addComponent(select);
    }

    @Override
    protected String getTestDescription() {
        return "Keyboard selecting of a value is broken in combobox if two "
                + "items have the same caption. The first item's id is \"one-1\" "
                + "while the second one is \"one-2\". Selecting with mouse works "
                + "as expected but selecting with keyboard always returns the "
                + "object \"one-1\".";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6125;
    }
}
