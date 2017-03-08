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
package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.ui.Table;

public class ProgrammaticUnselectInRange extends TestBase {

    private static final String PROPERTY = "property";

    final Label selectionLabel = new Label();
    final Table table = new Table();

    @Override
    protected void setup() {
        table.addContainerProperty(PROPERTY, Integer.class, "");

        table.setMultiSelect(true);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setPageLength(5);

        for (int i = 0; i < 5; i++) {
            Integer value = Integer.valueOf(i + 1);
            table.addItem(new Object[] { value }, value);
        }
        table.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                updateSelectionLabel();
            }
        });

        addComponent(table);
        addComponent(selectionLabel);
        addComponent(new Button("Deselect item 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.unselect(Integer.valueOf(2));
            }
        }));

        updateSelectionLabel();
    }

    private void updateSelectionLabel() {
        if (table.isSelected(Integer.valueOf(2))) {
            selectionLabel.setValue("Item 2 is selected");
        } else {
            selectionLabel.setValue("Item 2 is not selected");
        }
    }

    @Override
    protected String getDescription() {
        return "Selecting items 1 - 3 using shift click, deselecting item 2 using the button and selecting item 5 using ctrl should keep item 2 deselected according to the server";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
