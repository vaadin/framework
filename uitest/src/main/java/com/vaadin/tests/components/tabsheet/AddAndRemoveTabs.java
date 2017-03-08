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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;

public class AddAndRemoveTabs extends TestBase {
    private TabSheet tabSheet;

    private int counter = 0;

    @Override
    public void setup() {
        tabSheet = new TabSheet();
        addTab();
        addComponent(tabSheet);

        Button addTabBtn = new Button("Add new tab",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        addTab();
                    }

                });
        addComponent(addTabBtn);
    }

    private void addTab() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setCaption("Test " + counter);

        Button closeTab = new Button("Close tab", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabSheet.removeComponent(layout);

            }

        });

        layout.addComponent(closeTab);

        tabSheet.addComponent(layout);
        counter++;
    }

    @Override
    protected String getDescription() {
        return "Removing all tabs and then adding new tabs should work properly and without javascript errors. All new tabs should be displayed and not only the first one";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2861;
    }

}
