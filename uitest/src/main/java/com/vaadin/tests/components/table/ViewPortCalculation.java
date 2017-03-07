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
import com.vaadin.v7.data.Item;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CellStyleGenerator;

public class ViewPortCalculation extends TestBase {

    private Object lastDoubleClickedItemId;

    @Override
    protected void setup() {
        getLayout().setSpacing(true);
        addComponent(createTestTable(10));
    }

    @Override
    protected String getDescription() {
        return "Table rows that are too far down (but still visible) don't get focus after refreshRowCache/select (double-click)."
                + "<br> Double-clicking on the seventh (or any further) row of causes focus to jump to the first row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8298;
    }

    private Table createTestTable(int rows) {
        final Table table = new Table();
        table.setId("table");
        table.setSelectable(true);
        table.setPageLength(0);

        table.addContainerProperty("col1", String.class, null);
        table.addContainerProperty("col2", String.class, null);
        table.addContainerProperty("col3", String.class, null);

        for (int i = 1; i <= rows; ++i) {
            testData(table.addItem("row" + i), i);
        }

        table.setCellStyleGenerator(new CellStyleGenerator() {
            @Override
            public String getStyle(Table source, Object itemId,
                    Object propertyId) {
                if (itemId.equals(lastDoubleClickedItemId)) {
                    return "bold";
                }
                return null;
            }
        });

        table.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    lastDoubleClickedItemId = event.getItemId();
                    table.refreshRowCache();
                    table.select(event.getItemId());
                }
            }
        });
        return table;
    }

    private void testData(Item item, int i) {
        item.getItemProperty("col1").setValue("test1-" + i);
        item.getItemProperty("col2").setValue("test2-" + i);
        item.getItemProperty("col3").setValue("test3-" + i);
    }

}
