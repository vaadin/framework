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
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class RowAdditionTest extends TestBase {

    @Override
    protected String getDescription() {
        return "Adding a row should refresh client area only if newly added row is in the rendered area.";
    }

    @Override
    protected Integer getTicketNumber() {
        return new Integer(2799);
    }

    @Override
    protected void setup() {
        final Table table = new Table();
        final IndexedContainer container = (IndexedContainer) table
                .getContainerDataSource();
        table.addContainerProperty("column1", String.class, "test");

        for (int i = 0; i < 100; ++i) {
            table.addItem();
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(new Button("Add first", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Item item = container.addItemAt(0, new Object());
                item.getItemProperty("column1").setValue("0");
            }
        }));
        hl.addComponent(
                new Button("Add at position 50", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Item item = container.addItemAt(50, new Object());
                        item.getItemProperty("column1").setValue("50");
                    }
                }));
        hl.addComponent(
                new Button("Add at position 100", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Item item = container.addItemAt(100, new Object());
                        item.getItemProperty("column1").setValue("100");
                    }
                }));

        getLayout().addComponent(table);
        getLayout().addComponent(hl);
    }
}
