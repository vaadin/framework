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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetTabTheming extends TestBase {

    @Override
    public void setup() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setStyleName("pg");
        tabsheet.addTab(new Label(), "Brown fox and the fence", null);
        tabsheet.addTab(new Label(), "Something about using all the keys",
                null);
        addComponent(tabsheet);
        setTheme("tests-tickets");
    }

    @Override
    protected String getDescription() {
        return "Changing tabs should not cause flickering, cut text or text that moves back and forth.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6781;
    }
}
