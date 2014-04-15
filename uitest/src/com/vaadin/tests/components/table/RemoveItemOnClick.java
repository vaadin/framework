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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

public class RemoveItemOnClick extends AbstractTestUI {

    private Table table;

    @Override
    protected void setup(VaadinRequest request) {
        table = new Table();
        IndexedContainer ic = new IndexedContainer();
        populate(ic);
        table.setContainerDataSource(ic);
        table.setPageLength(20);
        table.setSelectable(true);
        table.setImmediate(true);
        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.removeItem(table.getValue());
            }
        });
        addComponent(table);
    }

    private void populate(Container container) {
        container.addContainerProperty("property1", String.class, "foo");
        container.addContainerProperty("property2", Integer.class, 1210);
        container.addContainerProperty("property3", String.class, "bar");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("Item " + i);
            item.getItemProperty("property1").setValue("This is item " + i);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Selecting a row should remove that row from the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10071;
    }

}
