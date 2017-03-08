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
import com.vaadin.v7.ui.Table;

public class SortLongTable extends AbstractTestCase {

    @Override
    public void init() {
        final int NUMBER_OF_ROWS = 100; // Works with 10

        LegacyWindow mainWindow = new LegacyWindow("Table Sort Test");
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);

        Table ptable = new Table();
        ptable.addContainerProperty("Sort_me_please", String.class, "--");
        for (int i = NUMBER_OF_ROWS - 1; i >= 0; i--) {
            ptable.addItem("" + i).getItemProperty("Sort_me_please")
                    .setValue("Value " + String.format("%02d", i));
        }

        ptable.setWidth("100%");
        ptable.setPageLength(NUMBER_OF_ROWS);

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(ptable);
        mainWindow.addComponent(vl);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the header should sort the column. It should not cause the headers to be scrolled out of view.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6367;
    }

}
