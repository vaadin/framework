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
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class UpdateTableWhenUnfocused extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = createTable();

        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(table, "tab1");
        tabSheet.setHeight("5000px");
        tabSheet.setWidth("100%");
        addComponent(tabSheet);

        final Button button = new Button("Refresh table");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                button.focus();
                table.refreshRowCache();
            }
        });
        addComponent(button);

    }

    private Table createTable() {
        Table table = new Table("Table");
        table.setImmediate(true);
        table.setMultiSelect(true);
        table.setSizeFull();
        table.setSelectable(true);

        Container ds = new IndexedContainer();
        ds.addContainerProperty("column", Integer.class, null);
        for (int i = 0; i < 500; i++) {
            Item item = ds.addItem(i);
            item.getItemProperty("column").setValue(i);
        }
        table.setContainerDataSource(ds);

        return table;
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button after selecting a row in the table should not cause the window to scroll.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12976;
    }

}
