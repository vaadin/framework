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
package com.vaadin.tests.elements;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;

public class TestBenchElementRightClick extends AbstractTestUI {

    Table table = new Table();
    Label labelEvent = new Label();
    private int COLUMNS = 4;
    private int ROWS = 10;

    @Override
    protected void setup(VaadinRequest request) {
        labelEvent.setId("label1");
        table.setId("id1");
        fillTable(table);
        addComponent(table);
        addComponent(labelEvent);
        labelEvent.setValue("InitialValue");
        table.addItemClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton().equals(MouseButton.RIGHT)) {
                    labelEvent.setValue("RightClick");
                } else if (event.isDoubleClick()) {
                    labelEvent.setValue("DoubleClick");
                }
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Test double click and right click on TestBenchElement";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14384;
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
