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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class WideSelectableTable extends TestBase {

    @Override
    protected void setup() {
        final int NUMBER_OF_COLS = 50;

        // mainWindow.setSizeFull();
        // setMainWindow(mainWindow);

        Table ptable = new Table();
        for (int colcount = 0; colcount < NUMBER_OF_COLS; colcount++) {
            String col = "COL_" + colcount + "";
            ptable.addContainerProperty(col, String.class, "--");
            ptable.addItem(colcount + "-").getItemProperty(col)
                    .setValue("--" + colcount + "");
        }
        ptable.setSelectable(true);
        ptable.setMultiSelect(true);
        ptable.setColumnReorderingAllowed(false);
        ptable.setImmediate(true);

        ptable.setWidth("100%");
        ptable.setPageLength(5);

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(ptable);
        addComponent(vl);
    }

    @Override
    protected String getDescription() {
        return "A wide table scrolls to the beginning when sorting a column at  the beginning when sorting a column at the end";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6788;
    }
}
