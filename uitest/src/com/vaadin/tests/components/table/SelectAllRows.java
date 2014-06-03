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
package com.vaadin.tests.components.table;

import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SelectAllRows extends AbstractTestUI {

    static final String TABLE = "table";
    static final String COUNT_SELECTED_BUTTON = "button";
    static final int TOTAL_NUMBER_OF_ROWS = 300;
    static final String COUNT_OF_SELECTED_ROWS_LABEL = "label";

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        final Table table = new Table();
        table.setId(TABLE);
        table.setImmediate(true);
        table.setMultiSelect(true);
        table.setSelectable(true);
        table.addContainerProperty("row", String.class, null);
        layout.addComponent(table);

        Button button = new Button("Count");
        button.setId(COUNT_SELECTED_BUTTON);
        layout.addComponent(button);

        final Label label = new Label();
        label.setId(COUNT_OF_SELECTED_ROWS_LABEL);
        label.setCaption("Selected count:");
        layout.addComponent(label);

        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Set selected = (Set) table.getValue();
                label.setValue(String.valueOf(selected.size()));
            }
        });

        for (int i = 0; i < TOTAL_NUMBER_OF_ROWS; i++) {
            Object itemId = table.addItem();
            table.getContainerProperty(itemId, "row").setValue("row " + i);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Selecting all rows does not work by selecting first row, press shift then select last row";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13008;
    }

}
