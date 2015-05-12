/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

public class TableNavigationPageDown extends AbstractTestUI {

    private final static int ROW_NUMBER = 50;

    @Override
    protected void setup(VaadinRequest req) {
        Table table = new Table();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setHeight("150px");

        table.addContainerProperty("num", Integer.class, "num");
        table.addContainerProperty("Foo", String.class, "Foov");
        table.addContainerProperty("Bar", String.class, "Barv");

        for (int i = 0; i < ROW_NUMBER; i++) {
            Object key = table.addItem();
            table.getItem(key).getItemProperty("num").setValue(i);
        }

        addComponent(table);

    }

    @Override
    protected String getTestDescription() {
        return "Navigation in Table with PageDown/PageUp/Home/End keys should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15332;
    }

}
