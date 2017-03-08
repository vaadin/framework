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

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxInvalidNullSelection extends TestBase {

    private boolean biggerData = true;
    private ComboBox<String> combo;
    private Log log = new Log(5);

    @Override
    protected void setup() {

        Button b = new Button("Swap data provider");
        b.addClickListener(event -> {
            if (biggerData) {
                combo.setItems("Item 3");
            } else {
                combo.setItems("Item 1", "Item 2", "Item 3", "Item 4");
            }
            biggerData = !biggerData;
        });

        combo = new ComboBox<>();
        combo.setItems("Item 1", "Item 2", "Item 3", "Item 4");
        combo.addValueChangeListener(
                event -> log.log("Value is now: " + combo.getValue()));
        addComponent(log);
        addComponent(b);
        addComponent(combo);
        addComponent(new Button("Dummy for TestBench"));
    }

    @Override
    protected String getDescription() {
        return "Select \"Item 3\" in the ComboBox, change the data provider, focus and blur the ComboBox. The value should temporarily change to null when changing data provider but not when focusing and blurring the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6170;
    }

}
