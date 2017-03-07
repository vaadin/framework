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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.ui.Table;

public class TableHeaderZoom extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setHeight("400px");
        table.setWidth("400px");
        table.addContainerProperty("Column 1", String.class, "");
        table.addContainerProperty("Column 2", String.class, "");

        for (int i = 0; i < 100; ++i) {
            table.addItem(new Object[] { "" + i, "foo" }, i);
        }

        LegacyWindow main = getMainWindow();
        main.setContent(new CssLayout());
        main.addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Table header text/icon disappears when zooming out";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6870;
    }
}
