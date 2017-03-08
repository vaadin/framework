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
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

public class TableWithChildComponents extends TestBase
        implements ClickListener {

    private static final String COL2 = "Column 2 - generated";
    private static final String COL1 = "Column 1 - components";
    private Log log = new Log(10);

    @Override
    protected void setup() {
        Table table = new Table();
        table.setWidth("500px");
        table.setPageLength(10);
        table.addContainerProperty(COL1, Component.class, null);
        table.addContainerProperty(COL2, Component.class, null);

        table.addGeneratedColumn(COL2, new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("Item id: " + itemId + " column: " + columnId,
                        TableWithChildComponents.this);
            }
        });

        for (int i = 0; i < 100; i++) {
            Item item = table.addItem("Row " + i);
            item.getItemProperty(COL1)
                    .setValue(new NativeButton("Row " + i + " native", this));
        }

        addComponent(table);
        addComponent(log);

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        log.log("Click on " + event.getButton().getCaption());

    }

}
