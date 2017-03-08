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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class SetCurrentPageFirstItemId extends TestBase {
    int index = 0;

    private final Table table = new Table();

    @Override
    public void setup() {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);

        getMainWindow().setContent(mainLayout);

        mainLayout.addComponent(table);
        table.setSizeFull();
        table.addContainerProperty("rowID", Integer.class, null);
        for (int i = 0; i < 20; i++) {
            addRow();
        }

        Button addrowButton = new Button("Add row");
        addrowButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent pEvent) {
                Object id = addRow();
                table.setCurrentPageFirstItemId(id);
            }
        });

        mainLayout.addComponent(addrowButton);
    }

    private Object addRow() {
        return table.addItem(new Object[] { index++ }, null);
    }

    @Override
    protected String getDescription() {
        return "Table.setCurrentPageFirstItemId doesn't always work with full sized Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7607;
    }
}
