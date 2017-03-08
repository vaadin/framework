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
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.v7.ui.Table;

public class TableInSubWindowMemoryLeak extends TestBase {

    @Override
    public void setup() {
        final Label label = new Label("Hello Vaadin user");
        addComponent(label);
        final Button openButton = new Button("open me");
        openButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                final Table table = new Table();
                window.setContent(table);
                UI.getCurrent().addWindow(window);
                window.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClose(final CloseEvent e) {
                        window.setContent(new Label());
                        UI.getCurrent().removeWindow(window);
                    }
                });
            }
        });
        addComponent(openButton);

        final Button openButton2 = new Button("open me without Table");
        openButton2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                UI.getCurrent().addWindow(window);
                window.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClose(final CloseEvent e) {
                        UI.getCurrent().removeWindow(window);
                    }
                });
            }
        });
        addComponent(openButton2);
    }

    @Override
    protected String getDescription() {
        return "IE 8 leaks memory with a subwindow containing a Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9197;
    }
}
