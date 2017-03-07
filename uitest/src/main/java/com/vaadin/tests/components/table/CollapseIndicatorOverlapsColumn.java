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
import com.vaadin.v7.ui.Table;

public class CollapseIndicatorOverlapsColumn extends TestBase {

    @Override
    protected void setup() {
        Table tbl = createTable();
        tbl = createTable();
        tbl.setWidth("400px");
        addComponent(tbl);
    }

    private Table createTable() {
        Table tbl = new Table();
        tbl.addContainerProperty("COL1", String.class, "Column 1");
        tbl.addContainerProperty("COL2", String.class, "Column 2");

        // Right align last column
        tbl.setColumnAlignment("COL2", Table.ALIGN_RIGHT);

        // Allow collapsing
        tbl.setColumnCollapsingAllowed(true);

        for (int i = 0; i < 5; i++) {
            Item item = tbl.addItem("Item " + i);
            for (int j = 1; j <= 2; j++) {
                item.getItemProperty("COL" + j).setValue("Item " + i + "/" + j);
            }
        }
        return tbl;
    }

    @Override
    protected String getDescription() {
        return "The rightmost column should not be covered by the collapse indicator";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6934;
    }

}
