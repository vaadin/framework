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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Table;

public class TableSetUndefinedSize extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSizeFull();

        Table table = createTable();
        table.setSizeFull();
        addComponent(table);

        HorizontalLayout widthButtons = createWidthButtons(table);
        addComponent(widthButtons);

        HorizontalLayout heightButtons = createHeigthButtons(table);
        addComponent(heightButtons);

        addComponent(createSizeUndefinedButton(table));
    }

    private Button createSizeUndefinedButton(final Table table) {
        return new Button("size undefined", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.setSizeUndefined();
            }
        });
    }

    private HorizontalLayout createWidthButtons(final Table table) {
        HorizontalLayout layout = new HorizontalLayout();

        layout.addComponent(
                new Button("width 500px", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setWidth("500px");
                    }
                }));
        layout.addComponent(
                new Button("width 100%", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setWidth("100%");
                    }
                }));
        layout.addComponent(
                new Button("undefined width", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setWidthUndefined();
                    }
                }));

        return layout;
    }

    private HorizontalLayout createHeigthButtons(final Table table) {
        HorizontalLayout layout = new HorizontalLayout();

        layout.addComponent(
                new Button("height 200px", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setHeight("200px");
                    }
                }));
        layout.addComponent(
                new Button("height 300px", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setHeight("300px");
                    }
                }));
        layout.addComponent(
                new Button("height 100%", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setHeight("100%");
                    }
                }));
        layout.addComponent(
                new Button("undefined height", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        table.setHeightUndefined();
                    }
                }));

        return layout;
    }

    private Table createTable() {
        Table table = new Table("");

        table.addContainerProperty("column 1", String.class, "column 1 value");
        table.addContainerProperty("column 2", String.class, "column 2 value");
        table.addContainerProperty("column 3", String.class, "column 3 value");

        for (int i = 0; i < 5; i++) {
            table.addItem();
        }

        return table;
    }

    @Override
    protected String getTestDescription() {
        return "Table width and height changing to undefined doesn't update table size";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15384;
    }
}
