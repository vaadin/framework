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

public class FixedHeightTable extends TestBase {

    private static final long serialVersionUID = -929892889178757852L;
    Table table;
    VerticalLayout layout;

    @Override
    public void setup() {

        table = new Table();
        table.addContainerProperty("test", String.class, null);
        table.setSizeFull();
        // bug: settings rows to 16 or more => last line is not rendered at all
        // on the client-side.
        final int maxRows = 16;
        for (int i = 1; i <= maxRows; i++) {
            table.addItem(new Object[] { "" + i }, i);
        }

        getLayout().setHeight("400px");
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "The table contains 16 (1-16) rows which all should be visible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3814;
    }
}
