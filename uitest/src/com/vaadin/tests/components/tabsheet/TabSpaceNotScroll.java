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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * If the space is pressed on the tabs of a tabsheet the browser default scroll
 * behavior must be prevented.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TabSpaceNotScroll extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        for (int i = 0; i < 5; i++) {
            String caption = "Tab " + i;
            Component c = new Label(caption);
            tabSheet.addTab(c, caption);
        }

        addComponent(tabSheet);

        Label dontShowThis = new Label("Page scroll. This is bad.");

        VerticalLayout panel = new VerticalLayout();
        panel.setHeight("2000px");
        panel.addComponent(dontShowThis);
        panel.setComponentAlignment(dontShowThis, Alignment.MIDDLE_CENTER);

        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Pressing space on the tab should not scroll.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14320;
    }

}
