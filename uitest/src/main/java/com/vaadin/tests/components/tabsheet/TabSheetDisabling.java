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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;

public class TabSheetDisabling extends TestBase {

    private static final int NR_BUTTONS = 10;
    private Button buttons[] = new Button[NR_BUTTONS];
    private TabSheet tabSheet;

    @Override
    public void setup() {
        tabSheet = new TabSheet();
        for (int i = 0; i < NR_BUTTONS; i++) {
            if (i % 2 == 0) {
                buttons[i] = new Button("Disable this tab",
                        new ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {
                                Button b = event.getButton();
                                tabSheet.getTab(b).setEnabled(false);

                            }

                        });
            } else {
                buttons[i] = new Button("Hide this tab", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        tabSheet.getTab(b).setVisible(false);
                    }

                });
            }
            tabSheet.addTab(buttons[i]);
        }

        Button button = new Button("Enable/disable", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                tabSheet.setEnabled(!tabSheet.isEnabled());
            }
        });
        addComponent(tabSheet);
        addComponent(button);
    }

    @Override
    protected String getDescription() {
        return "Switching the tabsheet between disabled and enabled should not change which tab is selected. Disabling the open tab should select the first enabled tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2658;
    }

}
