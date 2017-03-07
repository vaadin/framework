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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.Table;

public class TableVisibleColumnsUpdate extends AbstractReindeerTestUI {

    private String[] cols1 = new String[] { "p1", "p2", "p3" };
    private String[] cols2 = new String[] { "p1", "p4", "p3" };
    private Table table;

    @Override
    protected String getTestDescription() {
        return "Columns should change between p1,p2,p3 and p1,p4,p3";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3139;
    }

    @Override
    protected void setup(VaadinRequest request) {
        table = new Table();
        table.setWidth("400px");
        table.setHeight("100px");
        table.setPageLength(100);
        table.addContainerProperty("p1", String.class, null);
        table.addContainerProperty("p2", String.class, null);
        table.addContainerProperty("p3", String.class, null);
        table.addContainerProperty("p4", String.class, null);

        for (int i = 0; i < 10; i++) {
            table.addItem(new Object[] { "a" + i, "b" + i, "c" + i, "X" + i },
                    "" + i);
        }

        addComponent(table);

        table.setVisibleColumns(cols1);
        // table.setColumnHeaders(headers1);

        Button updateButton = new Button("Change columns", new ClickListener() {
            private boolean one = true;

            @Override
            public void buttonClick(ClickEvent event) {
                table.setVisibleColumns((one ? cols2 : cols1));
                one = !one;
            }
        });
        addComponent(updateButton);
    }

}
