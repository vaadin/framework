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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class NestedTabSheetsHideTabs extends TestBase {

    TabSheet main;
    TabSheet sub;

    @Override
    public void setup() {
        addComponent(new Button("Toggle tabs", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                main.hideTabs(!main.areTabsHidden());
            }
        }));
        sub = new TabSheet();
        sub.addTab(newPage(21), "Page 21");
        sub.addTab(newPage(22), "Page 22");
        main = new TabSheet();
        main.addTab(newPage(1), "Page 1");
        main.addTab(sub, "Page 2 (TabSheet)");
        main.addTab(newPage(3), "Page 3");
        addComponent(main);
    }

    private static ComponentContainer newPage(final int number) {
        final VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label("Page " + number));
        return vl;
    }

    @Override
    protected String getDescription() {
        return "Setting hideTabs(true) for a TabSheet containing another TabSheet hides the nested TabSheet's tabs as well";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9294;
    }

}
