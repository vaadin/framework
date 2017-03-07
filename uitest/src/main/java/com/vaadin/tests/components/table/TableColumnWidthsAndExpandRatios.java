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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.Table;

public class TableColumnWidthsAndExpandRatios extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        final Table table = new Table();
        table.setSizeFull();

        table.addContainerProperty("column1", String.class, "Humpty");
        table.addContainerProperty("column2", String.class, "Dumpty");
        table.addContainerProperty("column3", String.class, "Doe");

        for (int row = 0; row < 100; row++) {
            table.addItem();
        }

        HorizontalLayout buttons = new HorizontalLayout();
        for (Object col : table.getContainerPropertyIds()) {
            buttons.addComponent(createResetButton(col, table));
        }

        addComponent(table);
        addComponent(buttons);
    }

    private NativeButton createResetButton(final Object property,
            final Table table) {
        return new NativeButton("Reset " + property + " width",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnWidth(property, -1);
                    }
                });
    }

    @Override
    protected String getDescription() {
        return "Changing column width to -1 should remove any previous size measurements";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7922;
    }

}
