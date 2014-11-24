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

/**
 * Test that column natural width is restored after removing expand ratio
 * 
 * @author Vaadin Ltd
 */

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;

public class TableAfterRemovingExpandRatios extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest req) {
        getLayout().setSizeFull();

        Table tableInitial = createTable();
        Table table = createTable();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(createSetExpandRatiosButton(table));
        buttons.addComponent(createUnsetExpandRatiosButton(table));
        buttons.addComponent(createAddItemButton(table));

        addComponent(tableInitial);
        addComponent(table);
        addComponent(buttons);
    }

    private Table createTable() {
        Table table = new Table();
        table.addContainerProperty("column1", String.class, "Humprtrtwwww");
        table.addContainerProperty("column2", String.class, "Dumpttttwwww");
        table.addContainerProperty("column3", String.class, "Dogtt");

        for (int row = 0; row < 4; row++) {
            table.addItem();
        }
        table.setWidth("500px");
        table.setHeight("300px");
        return table;

    }

    private NativeButton createSetExpandRatiosButton(final Table table) {
        NativeButton button = new NativeButton("Set expand",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnExpandRatio("column1", 1.0f);
                        table.setColumnExpandRatio("column2", 3.0f);

                    }
                });
        button.setId("expand-button");
        return button;
    }

    private NativeButton createUnsetExpandRatiosButton(final Table table) {
        NativeButton button = new NativeButton("Unset expand",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnExpandRatio("column1", -1);
                        table.setColumnExpandRatio("column2", -1);
                    }
                });
        button.setId("unexpand-button");
        return button;
    }

    private NativeButton createAddItemButton(final Table table) {
        NativeButton button = new NativeButton("Add item",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.addItem();
                    }
                });
        button.setId("add-button");
        return button;

    }

    @Override
    protected String getTestDescription() {
        return "On setting expand ratios to false previous column widths should be restored";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15101;
    }

}
