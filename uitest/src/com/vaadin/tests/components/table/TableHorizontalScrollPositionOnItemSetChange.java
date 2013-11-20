/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableHorizontalScrollPositionOnItemSetChange extends
        AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        final Table table = new Table();
        table.setWidth("640px");
        table.setHeight("243px");
        table.setId("horscrolltable");
        layout.addComponent(table);

        for (int i = 0; i < 15; i++) {
            table.addContainerProperty("Column " + i, String.class, null);
        }

        for (int i = 0; i < 60; i++) {
            table.addItem();
        }

        Button lessItems = new Button("Less items", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.removeAllItems();
                for (int i = 0; i < 5; i++) {
                    table.addItem();
                }
            }
        });
        lessItems.setId("lessitems");

        Button moreItems = new Button("More items", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.removeAllItems();
                for (int i = 0; i < 50; i++) {
                    table.addItem();
                }
            }
        });
        moreItems.setId("moreitems");

        Button clearItems = new Button("Clear all", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.removeAllItems();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        layout.addComponent(buttonLayout);

        buttonLayout.addComponent(lessItems);
        buttonLayout.addComponent(moreItems);
        buttonLayout.addComponent(clearItems);
        clearItems.setId("clearitems");
    }

    @Override
    protected String getTestDescription() {
        return "Horizontal scrolling position should not be lost if amount of items changes in Table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12652;
    }

}
