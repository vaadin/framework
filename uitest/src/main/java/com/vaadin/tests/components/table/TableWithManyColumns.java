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
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

public class TableWithManyColumns extends TestBase {

    private static final int ROWS = 20;
    private static final int COLS = 100;

    @Override
    protected void setup() {
        Table t = new Table();

        for (int i = 0; i < COLS; i++) {
            t.addContainerProperty("COLUMN_" + i, String.class, "");
        }
        for (int row = 0; row < ROWS; row++) {
            Item i = t.addItem(String.valueOf(row));
            for (int col = 0; col < COLS; col++) {
                Property<String> p = i.getItemProperty("COLUMN_" + col);
                p.setValue("item " + row + "/" + col);
            }
        }
        t.setFooterVisible(true);
        t.setSizeFull();
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "The footer, header and content cells should be as wide, even when the Table contains many columns";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5185;
    }

}
