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
package com.vaadin.tests.elements.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class TableScroll extends AbstractTestUI {

    Table table;
    public static final int COLUMNS = 5;
    public static final int ROWS = 100;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("200px");
        layout.setHeight("200px");

        Table table = new Table();
        table.setWidth("200px");
        table.setHeight("200px");
        initProperties(table);
        fillTable(table);
        layout.addComponent(table);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Test table element scroll and scrollLeft";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13826;
    }

    // set up the properties (columns)
    private void initProperties(Table table) {
        for (int i = 0; i < COLUMNS; i++) {
            table.addContainerProperty("property" + i, String.class,
                    "some value");
        }
    }

    // fill the table with some random data
    private void fillTable(Table table) {
        initProperties(table);
        for (int i = 0; i < ROWS; i++) {
            String[] line = new String[COLUMNS];
            for (int j = 0; j < COLUMNS; j++) {
                line[j] = "col=" + j + " row=" + i;
            }
            table.addItem(line, null);
        }
    }
}
