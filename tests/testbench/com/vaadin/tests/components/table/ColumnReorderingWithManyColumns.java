/*
 * Copyright 2012 Vaadin Ltd.
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
import com.vaadin.ui.Table;

public class ColumnReorderingWithManyColumns extends TestBase {

    private static final int NUM_COLS = 16;

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSizeFull();
        table.setColumnReorderingAllowed(true);

        for (int i = 0; i < NUM_COLS; ++i) {
            table.addContainerProperty("col-" + i, String.class, null);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "When reordering columns via drag'n'drop, the drop marker is drawn too far to the right.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10890;
    }
}
