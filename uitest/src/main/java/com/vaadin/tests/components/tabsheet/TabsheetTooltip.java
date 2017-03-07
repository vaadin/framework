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

import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabsheetTooltip extends TestBase {

    @Override
    protected String getDescription() {
        return "The label inside the tabsheet should show a tooltip 'This is a label' and the tab should show a different tooltip 'This is a tab'";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2995;
    }

    @Override
    protected void setup() {
        TabSheet tabSheet = new TabSheet();
        Label l = new Label("Label");
        l.setDescription("This is a label");

        Tab tab = tabSheet.addTab(l, "Tab", null);
        tab.setDescription("This is a tab");
        tab.setComponentError(new UserError("abc error"));

        Tab tab2 = tabSheet.addTab(new Label("Another label, d'oh"), "Tab 2",
                null);
        tab2.setDescription("This is another tab");

        addComponent(tabSheet);
    }
}
