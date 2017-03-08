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
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class PreventTabChange extends TestBase
        implements SelectedTabChangeListener {

    private TabSheet tabSheet;

    private Component lastTab;

    private Label tab1;
    private Label tab2;
    private Label tab3;

    @Override
    protected String getDescription() {
        return "Tests prevention of selecting certain tabs. Selecting the tabs in order (1-2-3-1) should work, while selecting out of order should cause the current tab to remain selected. The selected tab will actually first be changed (by the client) and then changed back (on the server response).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3199;
    }

    @Override
    protected void setup() {
        tabSheet = new TabSheet();
        tabSheet.setId("tabsheet");
        tabSheet.addSelectedTabChangeListener(this);
        tab1 = new Label("Tab 1 contents");
        tab2 = new Label("Tab 2 contents");
        tab3 = new Label("Tab 3 contents");

        tabSheet.addTab(tab1, "The first tab", null);
        tabSheet.addTab(tab2, "The second tab", null);
        tabSheet.addTab(tab3, "The third tab", null);

        lastTab = tab1;
        tabSheet.setSelectedTab(tab1);

        addComponent(tabSheet);
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        TabSheet tabsheet = event.getTabSheet();

        if (lastTab == tab1) {
            if (tabsheet.getSelectedTab() != tab2) {
                tabsheet.setSelectedTab(lastTab);
            }
        } else if (lastTab == tab2) {
            if (tabsheet.getSelectedTab() != tab3) {
                tabsheet.setSelectedTab(lastTab);
            }
        } else if (lastTab == tab3) {
            if (tabsheet.getSelectedTab() != tab1) {
                tabsheet.setSelectedTab(lastTab);
            }
        }

        lastTab = tabsheet.getSelectedTab();
    }
}
