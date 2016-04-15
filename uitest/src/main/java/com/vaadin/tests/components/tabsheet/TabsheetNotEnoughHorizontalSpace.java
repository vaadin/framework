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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

/**
 * Test to see if tabsheet navigation buttons render correctly in Chameleon
 * 
 * @author Vaadin Ltd
 */
@Theme("chameleon")
public class TabsheetNotEnoughHorizontalSpace extends AbstractTestUI {

    private TabSheet tabsheet = new TabSheet();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        generateTabs();
        tabsheet.setSizeFull();
        addComponent(tabsheet);

    }

    private void generateTabs() {
        tabsheet.removeAllComponents();
        for (int i = 0; i < 100; ++i) {
            tabsheet.addTab(new Panel(), "Tab" + i);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Scroll-buttons should render correctly on all browsers";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12154;
    }

}
