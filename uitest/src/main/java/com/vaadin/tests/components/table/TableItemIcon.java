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

import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class TableItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2457;
    }

    @Override
    protected String getDescription() {
        return "The items in the Table should have icons in the first column (rowheader).";
    }

    @Override
    protected void setup() {
        Table table = new Table();
        table.addContainerProperty("icon", Resource.class, null);
        table.setItemIconPropertyId("icon");
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        getLayout().addComponent(table);

        Item item = table.addItem("FI");
        item.getItemProperty("icon").setValue(new ClassResource("fi.gif"));
        item = table.addItem("SE");
        item.getItemProperty("icon").setValue(new ClassResource("se.gif"));

    }

}
