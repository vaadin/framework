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
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class TableScrollsOnSelection extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setSizeUndefined();

        IndexedContainer cont = new IndexedContainer();
        cont.addContainerProperty("number", String.class, null);
        for (int i = 0; i < 80; i++) {
            Item item = cont.addItem(i);
            item.getItemProperty("number").setValue(i + "");
        }
        Table table = new Table();
        table.setPageLength(0);
        table.setContainerDataSource(cont);
        table.setSelectable(true);
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "The scroll position should not change when an item is selected in a Table that is higher than the view.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6197;
    }
}
