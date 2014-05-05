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
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetHotKeysWithModifiers extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("500px");
        tabSheet.setHeight("500px");
        tabSheet.addTab(new Label("Tab 1"), "Tab 1").setClosable(true);
        tabSheet.addTab(new Label("Tab 2"), "Tab 2").setClosable(true);
        tabSheet.addTab(new Label("Tab 3"), "Tab 3").setClosable(true);
        setContent(tabSheet);
    }

    @Override
    protected String getTestDescription() {
        return "Hot keys (left and right arrow keys and the delete key) should be ignored when they are pressed simultaneously with modifier keys";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12178;
    }

}
