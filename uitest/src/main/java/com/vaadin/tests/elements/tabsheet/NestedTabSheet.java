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
package com.vaadin.tests.elements.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;

/**
 * This UI contains a nested tab sheet, i.e., there is a tab sheet that contains
 * other tab sheets as its components.
 */
public class NestedTabSheet extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet outer = new TabSheet();
        TabSheet firstInner = new TabSheet();
        firstInner.addTab(new CssLayout(), "Tab 1.1");
        firstInner.addTab(new CssLayout(), "Tab 1.2");
        firstInner.addTab(new CssLayout(), "Tab 1.3");
        TabSheet secondInner = new TabSheet();
        secondInner.addTab(new CssLayout(), "Tab 2.1");
        TabSheet thirdInner = new TabSheet();
        thirdInner.addTab(new CssLayout(), "Tab 3.1");
        thirdInner.addTab(new CssLayout(), "Tab 3.2");
        outer.addTab(firstInner, "Tab 1");
        outer.addTab(secondInner, "Tab 2");
        outer.addTab(thirdInner, "Tab 3");
        addComponent(outer);
    }

    @Override
    protected String getTestDescription() {
        return "TestBench should not select tabs from an inner tab sheet in"
                + " calls involving the outer tab sheet.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13735;
    }
}