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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetErrorTooltip extends AbstractTestUI {

    private TabSheet tabSheet = new TabSheet();
    private int tabCount = 0;

    @Override
    protected void setup(VaadinRequest request) {

        addTab();
        addTab().setComponentError(new UserError("Error!"));
        addTab().setDescription("This is a tab");

        Tab t = addTab();
        t.setComponentError(new UserError("Error!"));
        t.setDescription("This tab has both an error and a description");

        setContent(tabSheet);
        getTooltipConfiguration().setOpenDelay(0);
        getTooltipConfiguration().setQuickOpenDelay(0);
        getTooltipConfiguration().setCloseTimeout(1000);
    }

    private Tab addTab() {
        tabCount++;
        Label contents = new Label("Contents for tab " + tabCount);
        return tabSheet.addTab(contents, "Tab " + tabCount);
    }

    @Override
    protected String getTestDescription() {
        return "TabSheet Tabs should display component error tooltips when expected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12802;
    }

}
