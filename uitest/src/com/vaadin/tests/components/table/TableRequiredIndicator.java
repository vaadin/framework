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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

public class TableRequiredIndicator extends AbstractTestUI {

    static final String TABLE = "table";
    static final String COUNT_SELECTED_BUTTON = "button";
    static final int TOTAL_NUMBER_OF_ROWS = 300;
    static final String COUNT_OF_SELECTED_ROWS_LABEL = "label";

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.setId(TABLE);
        table.setSelectable(true);
        table.addContainerProperty("row", String.class, null);
        for (int i = 0; i < TOTAL_NUMBER_OF_ROWS; i++) {
            Object itemId = table.addItem();
            table.getContainerProperty(itemId, "row").setValue("row " + i);
        }
        addComponent(table);

        // This should cause red asterisk to the vertical layout
        table.setRequired(true);

        table.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = table.getValue();
                if (value != null) {
                    Notification.show("Value is set.");
                } else {
                    Notification.show("Value is NOT set.");
                }

            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Table is required and should have red asterisk to indicate that.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13008;
    }

}
