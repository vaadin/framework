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
package com.vaadin.tests.components.select;

import com.vaadin.event.FieldEvents;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.ComboBox;

public class FocusListenerBreaksDropdownMenu extends TestBase {

    @Override
    protected void setup() {
        final ComboBox comboBox = new ComboBox();
        for (int i = 0; i < 5; ++i) {
            comboBox.addItem("Item " + i);
        }

        comboBox.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FieldEvents.FocusEvent event) {
                comboBox.addItem();
            }
        });

        comboBox.setImmediate(true);
        addComponent(comboBox);
    }

    @Override
    protected String getDescription() {
        return "Clicking the dropdown arrow on a not-already-focused ComboBox "
                + "breaks the dropdown list if a FocusListener adds or removes items";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8321;
    }

}
