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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxCombinedWithEnterShortcut extends TestBase {
    final String[] cities = new String[] { "Berlin", "Brussels", "Helsinki",
            "Madrid", "Oslo", "Paris", "Stockholm" };

    private Log log = new Log(5);

    @Override
    protected void setup() {
        final ComboBox l = new ComboBox("Please select a city");
        for (int i = 0; i < cities.length; i++) {
            l.addItem(cities[i]);
        }

        l.setFilteringMode(FilteringMode.OFF);
        l.setImmediate(true);
        l.setNewItemsAllowed(true);

        Button aButton = new Button("Show Value");
        aButton.setClickShortcut(KeyCode.ENTER);
        aButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Button clicked. ComboBox value: " + l.getValue());

            }

        });

        addComponent(log);
        addComponent(l);
        addComponent(aButton);
    }

    @Override
    protected String getDescription() {
        return "Button has Enter as click shortcut key. The shortcut should not be triggered when selecting an item in the dropdown";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6686;
    }

}
