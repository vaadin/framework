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
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class TooManySetColumnCollapsedCalls extends TestBase {

    private int counter = 0;
    private Label label;
    private Table table;

    @Override
    protected void setup() {
        label = new Label(String.valueOf(counter));
        label.setId("label");
        table = createTable();
        table.setId("table");
        addComponent(table);
        addComponent(label);
        getLayout().setSpacing(true);
        table.setColumnCollapsed("p2", true);
    }

    @Override
    protected String getDescription() {
        return "Table.setColumnCollapsed is called too many times in Table.changeVariables."
                + " Collapsing column 'P3' should only increase the counter by one.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5681;
    }

    private Table createTable() {
        Table table = new Table() {
            @Override
            public void setColumnCollapsed(Object propertyId, boolean collapsed)
                    throws IllegalStateException {
                ++counter;
                label.setValue(String.valueOf(counter));
                super.setColumnCollapsed(propertyId, collapsed);
            }
        };
        table.setWidth("400px");
        table.setHeight("100px");
        table.setPageLength(100);
        table.setColumnCollapsingAllowed(true);
        table.setImmediate(true);
        table.addContainerProperty("p1", String.class, null);
        table.addContainerProperty("p2", String.class, null);
        table.addContainerProperty("p3", String.class, null);
        table.addContainerProperty("p4", String.class, null);

        for (int i = 0; i < 10; i++) {
            table.addItem(new Object[] { "a" + i, "b" + i, "c" + i, "X" + i },
                    "" + i);
        }
        return table;
    }

}
