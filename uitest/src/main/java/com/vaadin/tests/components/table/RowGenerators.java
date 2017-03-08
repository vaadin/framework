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
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.GeneratedRow;
import com.vaadin.v7.ui.Table.RowGenerator;

public class RowGenerators extends TestBase implements RowGenerator {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setContainerDataSource(filledContainer());
        table.setRowGenerator(this);
        addComponent(table);
    }

    private Container filledContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty("Property 1", String.class, "");
        c.addContainerProperty("Property 2", String.class, "");
        c.addContainerProperty("Property 3", String.class, "");
        c.addContainerProperty("Property 4", String.class, "");
        for (int ix = 0; ix < 500; ix++) {
            Item i = c.addItem(ix);
            i.getItemProperty("Property 1").setValue("Item " + ix + ",1");
            i.getItemProperty("Property 2").setValue("Item " + ix + ",2");
            i.getItemProperty("Property 3").setValue("Item " + ix + ",3");
            i.getItemProperty("Property 4").setValue("Item " + ix + ",4");
        }
        return c;
    }

    @Override
    public GeneratedRow generateRow(Table table, Object itemId) {
        if ((Integer) itemId % 5 == 0) {
            if ((Integer) itemId % 10 == 0) {
                return new GeneratedRow(
                        "foobarbazoof very extremely long, most definitely will span.");
            } else {
                return new GeneratedRow("foo", "bar", "baz", "oof");
            }
        }
        return null;
    }

    @Override
    protected String getDescription() {
        return "Row generators should replace every fifth row in the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6720;
    }

}
