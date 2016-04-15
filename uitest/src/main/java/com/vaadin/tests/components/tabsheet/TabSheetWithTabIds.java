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

/**
 * 
 */
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetWithTabIds extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        final Tab tab1 = tabSheet.addTab(new Label("Label 1"), "Tab 1", null);

        final Tab tab2 = tabSheet.addTab(new Label("Label 2"), "Tab 2", null);

        final Tab tab3 = tabSheet.addTab(new Label("Label 3"), "Tab 3", null);

        addComponent(tabSheet);

        Button b = new Button("Set ids", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tab1.setId("tab1");
                tab2.setId("tab2");
                tab3.setId("tab3");
            }
        });
        addComponent(b);

        Button b2 = new Button("Clear ids", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tab1.setId(null);
                tab2.setId(null);
                tab3.setId(null);
            }
        });
        addComponent(b2);
    }

    @Override
    protected String getTestDescription() {
        return "Add support for setId to TabSheet.Tab";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12064;
    }

}
