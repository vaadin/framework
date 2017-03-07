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
import com.vaadin.v7.ui.Table;

public class SelectableEditable extends TestBase {

    @Override
    protected void setup() {
        // TODO Auto-generated method stub

        final Table table = new Table();
        table.setWidth("500px");
        table.setSelectable(true);
        table.setEditable(true);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("alive", Boolean.class, false);
        for (int i = 0; i < 10; ++i) {
            table.addItem(new Object[] { "Person " + i, false }, i);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return "It is difficult to select rows of an editable Table, especially columns with checkboxes.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return 9064;
    }
}
