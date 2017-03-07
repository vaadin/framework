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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.v7.ui.TextField;

public class TableColumnResizeContentsWidth extends AbstractReindeerTestUI {

    private static final String COL1 = "COL1";

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.addGeneratedColumn(COL1, new ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                TextField textField = new TextField();
                textField.setWidth("100%");
                return textField;
            }
        });

        table.addItem();

        table.setWidth("200px");
        table.setColumnWidth(COL1, 100);

        addComponent(table);
        addComponent(new Button("Increase width", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnWidth(COL1, table.getColumnWidth(COL1) + 20);
                table.markAsDirty();
            }
        }));
        addComponent(new Button("Decrease width", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnWidth(COL1, table.getColumnWidth(COL1) - 40);
                table.markAsDirty();
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "When a column is resized, it's contents should update to match the new size";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7393);
    }

}
