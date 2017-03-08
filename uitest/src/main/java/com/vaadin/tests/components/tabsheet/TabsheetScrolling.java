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
import com.vaadin.ui.TabSheet.Tab;

public class TabsheetScrolling extends TestBase {

    public static final String SELECT_FIRST = "selFirst";
    public static final String SELECT_LAST = "selLast";
    private TabSheet fixedSizeTabSheet;
    private TabSheet autoWideTabSheet;

    @Override
    protected void setup() {
        fixedSizeTabSheet = new TabSheet();
        fixedSizeTabSheet.setHeight("200px");
        fixedSizeTabSheet.setWidth("400px");

        for (int i = 0; i < 100; i++) {
            Button b = new Button("Hide this tab (" + i + ")",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            fixedSizeTabSheet.getTab(event.getButton())
                                    .setVisible(false);
                        }

                    });
            Tab t = fixedSizeTabSheet.addTab(b, "Tab " + i, null);
            if (i % 2 == 0) {
                t.setVisible(false);
            }
        }

        addComponent(fixedSizeTabSheet);

        autoWideTabSheet = new TabSheet();
        autoWideTabSheet.setHeight("200px");
        autoWideTabSheet.setWidth(null);

        for (int i = 0; i < 10; i++) {
            Button b = new Button("Hide this tab (" + i + ")",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            autoWideTabSheet.getTab(event.getButton())
                                    .setVisible(false);
                        }
                    });

            Tab t = autoWideTabSheet.addTab(b, "Tab " + i, null);
            if (i % 2 == 0) {
                t.setVisible(false);

            }
        }

        addComponent(autoWideTabSheet);
        Button selectFirst = new Button("Select first tab in both tabsheets",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        fixedSizeTabSheet.setSelectedTab(0);
                        autoWideTabSheet.setSelectedTab(0);
                    }
                });
        selectFirst.setId(SELECT_FIRST);
        addComponent(selectFirst);
        Button selectLast = new Button("Select last tab in both tabsheets",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        int lastFixed = fixedSizeTabSheet.getComponentCount()
                                - 1;
                        fixedSizeTabSheet.setSelectedTab(lastFixed);
                        int lastAuto = autoWideTabSheet.getComponentCount() - 1;
                        autoWideTabSheet.setSelectedTab(lastAuto);
                    }
                });
        selectLast.setId(SELECT_LAST);
        addComponent(selectLast);
    }

    @Override
    protected String getDescription() {
        return "Two tabsheets, upper has fixed width, lower has dynamic width. Every other tab in both tabsheets are hidden (even numbered tabs). Scrolling the upper tab sheet should never display a hidden tab. Hiding a tab in the upper tabsheet should not affect scrolling. Hiding a tab in the lower tabsheet should make the tabsheet width change (auto wide).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3141;
    }

}
