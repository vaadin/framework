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

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class TableExtraScrollbars extends AbstractTestCase {

    private static int PROPS = 15;
    private static int ROWS = 1000;

    @Override
    public void init() {
        setTheme("runo");
        LegacyWindow w = new LegacyWindow("Table scrollbars bug example");
        setMainWindow(w);

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(createTable());
        w.setContent(vl);
    }

    protected Table createTable() {
        Table table = new Table(null, createContainer());
        table.setSizeFull();
        table.setPageLength(50);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        return table;
    }

    protected Container createContainer() {
        Container container = new IndexedContainer();
        for (int i = 0; i < PROPS; ++i) {
            container.addContainerProperty("prop" + i, String.class, null);
        }
        for (int i = 0; i < ROWS; ++i) {
            Item item = container.addItem(i);
            for (int p = 0; p < PROPS; ++p) {
                item.getItemProperty("prop" + p)
                        .setValue("property value 1234567890");
            }
        }
        return container;
    }

    @Override
    protected String getDescription() {
        return "Scrolling down in the table should not add extra scrollbars";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4489;
    }
}
