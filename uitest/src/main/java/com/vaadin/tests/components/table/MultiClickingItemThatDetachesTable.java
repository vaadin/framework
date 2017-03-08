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
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;

@SuppressWarnings("serial")
public class MultiClickingItemThatDetachesTable extends TestBase {
    @Override
    public void setup() {
        final Table table = new Table();
        table.setImmediate(true);
        table.addContainerProperty("p1", String.class, "p1");
        table.addContainerProperty("p2", String.class, "p2");
        for (int i = 0; i < 200; ++i) {
            final Item item = table.getItem(table.addItem());
            item.getItemProperty("p2").setValue(i + "");
            item.getItemProperty("p1").setValue(i + "");
        }
        table.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    try {
                        // Wait a bit so there's time to click multiple times
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    replaceComponent(table, new Label("Completed!"));
                }
            }
        });
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Clicking multiple times on an item whose listener detaches the table causes Out of Sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8580;
    }

}
