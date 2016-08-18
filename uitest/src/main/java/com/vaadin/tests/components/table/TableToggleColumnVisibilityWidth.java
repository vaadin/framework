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
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

/**
 * Test that column width is restored after restoring column visibility
 */
@SuppressWarnings("serial")
public class TableToggleColumnVisibilityWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        final Table table = new Table();

        table.addContainerProperty("Name", String.class, "");
        table.addContainerProperty("Last Name", String.class, "");

        table.setColumnWidth("Name", 100);
        table.setColumnWidth("Last Name", 200);
        table.setHeight("200px");

        table.addItem(new Object[] { "Adam", "Morrison" }, new Integer(1));
        table.addItem(new Object[] { "Eva", "Roberts" }, new Integer(2));
        table.addItem(new Object[] { "Rob", "Spears" }, new Integer(3));
        table.addItem(new Object[] { "Bob", "Michigan" }, new Integer(4));
        table.setVisibleColumns(new Object[] { "Name", "Last Name" });

        final Button infoToggler = new Button("visibility");
        infoToggler.setId("toggler");
        infoToggler.addClickListener(new ClickListener() {
            private boolean detailed = true;

            @Override
            public void buttonClick(ClickEvent event) {
                if (detailed) {
                    table.setVisibleColumns(new Object[] { "Name" });
                } else {
                    table.setVisibleColumns(
                            new Object[] { "Name", "Last Name" });
                }
                detailed = !detailed;
            }
        });

        layout.addComponent(table);
        layout.addComponent(infoToggler);

        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Toggling visibility of table columns should not change the width of fixed sized columns";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12303;
    }

}
