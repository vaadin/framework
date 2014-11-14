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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * This testUI is used for testing that the scroll position of a tab sheet does
 * not change when tabs are removed. The exception is removing the leftmost
 * visible tab.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TabSheetScrollOnTabClose extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet tabSheet = new TabSheet();
        for (int i = 0; i < 10; i++) {
            Tab tab = tabSheet.addTab(new CssLayout(), "tab " + i);
            tab.setClosable(true);
            tab.setId("tab" + i);
        }
        tabSheet.setWidth(250, Unit.PIXELS);
        addComponent(tabSheet);
        addComponent(new Label("Close tab number"));
        for (int i = 0; i < 10; i++) {
            final String tabCaption = "tab " + i;
            final Button b = new Button("" + i);
            b.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    b.setEnabled(false);
                    tabSheet.removeTab(getTab(tabSheet, tabCaption));
                }
            });
            addComponent(b);
        }
    }

    private Tab getTab(TabSheet ts, String tabCaption) {
        for (int i = 0; i < ts.getComponentCount(); i++) {
            String caption = ts.getTab(i).getCaption();
            if (tabCaption.equals(caption)) {
                return ts.getTab(i);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Scroll position should not change when closing tabs.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14348;
    }
}