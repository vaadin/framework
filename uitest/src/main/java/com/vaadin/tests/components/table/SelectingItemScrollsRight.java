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
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class SelectingItemScrollsRight extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.setWidth("300px");
        table.setColumnWidth("Column", 500);
        table.addGeneratedColumn("Column", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Label(
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
        });

        for (int i = 0; i < 50; i++) {
            table.addItem();
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Clicking on an item that is longer than the table width should not scroll the table right";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5385;
    }

}
