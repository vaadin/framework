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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

public class SubWindowWithUndefinedHeight extends TestBase {

    @Override
    protected String getDescription() {
        return "Setting subwindow height to undefined after initial rendering does not update visual height";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7916;
    }

    @Override
    protected void setup() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Window subwindow = new Window("subwindow", layout);
        subwindow.center();
        subwindow.setSizeUndefined();
        layout.setSizeUndefined();

        final Button tabButton = new Button("A button");
        tabButton.setCaption("Tab 1");
        tabButton.setWidth("200px");

        final Table table = new Table();
        table.setCaption("tab 2");
        table.setWidth("100%");
        table.setHeight("100%");

        final TabSheet tabsheet = new TabSheet();
        tabsheet.addComponent(tabButton);
        tabsheet.addComponent(table);
        tabsheet.addSelectedTabChangeListener(
                new TabSheet.SelectedTabChangeListener() {
                    @Override
                    public void selectedTabChange(
                            TabSheet.SelectedTabChangeEvent event) {
                        if (tabsheet.getSelectedTab() == tabButton) {
                            tabsheet.setSizeUndefined();
                            layout.setSizeUndefined();
                            subwindow.setSizeUndefined();
                        } else if (tabsheet.getSelectedTab() == table) {
                            subwindow.setWidth("500px");
                            subwindow.setHeight("500px");
                            layout.setSizeFull();
                            tabsheet.setSizeFull();
                        }
                    }
                });
        layout.addComponent(tabsheet);

        Button button = new Button("click me", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getMainWindow().addWindow(subwindow);
            }
        });
        getMainWindow().addComponent(button);
    }
}
