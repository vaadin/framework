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

import java.util.ArrayList;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class TabKeyboardNavigationWaiAria extends AbstractReindeerTestUI {

    int index = 1;
    ArrayList<Component> tabs = new ArrayList<>();
    TabSheet ts = new TabSheet();

    @Override
    protected void setup(VaadinRequest request) {
        ts.setWidth("500px");
        ts.setHeight("500px");

        for (int i = 0; i < 5; ++i) {
            addTab();
        }

        Button addTab = new Button("Add a tab", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addTab();
            }
        });
        Button focus = new Button("Focus tabsheet", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ts.focus();
            }
        });

        addComponent(addTab);
        addComponent(focus);

        addComponent(ts);
    }

    @Override
    protected String getTestDescription() {
        return "The tab bar should be focusable and arrow keys should change focus for tabs. Space key selects a focused tab. The del key should close a tab if closable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11827;
    }

    private Tab addTab() {
        Layout content = new VerticalLayout();
        tabs.add(content);

        TextField field = new TextField("Tab " + index + " label");
        content.addComponent(field);

        Tab tab = ts.addTab(content, "Tab " + index, null);

        if (index == 2) {
            tab.setClosable(true);
            tab.setDescription("Tab 2 Tooltip");
        }

        if (index == 4) {
            tab.setEnabled(false);
        }

        if (index == 5) {
            tab.setDefaultFocusComponent(field);
        }

        index++;
        return tab;
    }
}
