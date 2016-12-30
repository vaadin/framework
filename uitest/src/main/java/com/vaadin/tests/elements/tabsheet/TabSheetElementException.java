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
 * this UI is used for testing that an exception occurs when TestBench attempts
 * to open a tab that does not exist.
 */
public class TabSheetElementException extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet ts = new TabSheet();
        for (int i = 1; i <= 5; i++) {
            ts.addTab(new CssLayout(), "Tab " + i);
        }
        addComponent(ts);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that an exception is thrown when TestBench attempts to"
                + " click a tab that does not exist.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13734;
    }
}