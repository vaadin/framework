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
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class ColumnExpandRatio extends TestBase {

    @Override
    protected String getDescription() {
        return "Column expand ratios can be used to adjust the way "
                + "how excess horizontal space is divided among columns.";

    }

    @Override
    protected Integer getTicketNumber() {
        return 2806;
    }

    private static final int ROWS = 100;

    @Override
    public void setup() {
        Table table1 = initTable();
        addComponent(new Label("Plain table"));
        addComponent(table1);

    }

    private Table initTable() {
        Table table = new Table();
        table.setWidth("100%");

        IndexedContainer idx = new IndexedContainer();
        idx.addContainerProperty("firstname", String.class, null);
        idx.addContainerProperty("lastname", String.class, null);
        Item i = idx.addItem(1);
        i.getItemProperty("firstname").setValue("John");
        i.getItemProperty("lastname").setValue("Johnson");

        i = idx.addItem(2);
        i.getItemProperty("firstname").setValue("Jane");
        i.getItemProperty("lastname").setValue("Janeine");

        for (int index = 3; index < ROWS; index++) {
            i = idx.addItem(index);
            i.getItemProperty("firstname").setValue("Jane");
            i.getItemProperty("lastname").setValue("Janeine");
        }

        idx.addContainerProperty("fixed 50px column", String.class, "");

        idx.addContainerProperty("Expanded with 2", String.class, "foobar");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName (1)");

        table.setColumnWidth("fixed 50px column", 50);
        table.setColumnExpandRatio("Expanded with 2", 2);
        table.setColumnExpandRatio("lastname", 1);

        return table;
    }

}
