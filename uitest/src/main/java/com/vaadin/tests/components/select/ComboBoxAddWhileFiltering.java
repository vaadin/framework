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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.ComboBox;

/**
 * TODO can't reproduce the issue with this test case, possibly need some
 * enhancements.
 *
 */
public class ComboBoxAddWhileFiltering extends TestBase {

    private int i;

    @Override
    protected void setup() {
        final ComboBox comboBox = new ComboBox();
        populate(comboBox);

        Button b = new Button("add item (^N)");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addItem(comboBox);
            }
        });
        addComponent(b);
        addComponent(comboBox);
        getMainWindow().addAction(new Button.ClickShortcut(b, "^n"));
    }

    private void populate(ComboBox comboBox) {
        for (i = 0; i < 4;) {
            addItem(comboBox);
        }
    }

    private void addItem(ComboBox comboBox) {
        i++;
        comboBox.addItem("Item " + i);

    }

    @Override
    protected String getDescription() {
        return "Filtered list should be updated when new item is added.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3643;
    }

}
