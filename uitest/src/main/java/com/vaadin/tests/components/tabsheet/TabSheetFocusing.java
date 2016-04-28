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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetFocusing extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet ts = new TabSheet();
        ts.setWidth("400px");
        ts.setHeight("200px");
        addComponent(ts);
        addComponent(new Button("Add tab", new ClickListener() {
            int i = 0;

            @Override
            public void buttonClick(ClickEvent event) {
                Tab t = ts.addTab(new Button("Tab " + ++i));
                ts.setSelectedTab(t);
                ts.focus();
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "The tab scroller should stay in place when tabs are focused using server-side calls.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12343;
    }

}
