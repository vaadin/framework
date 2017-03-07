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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabsheetScrollIntoView extends AbstractReindeerTestUI {

    public static final String BTN_SELECT_LAST_TAB = "showAndSelectLastTab";

    private TabSheet tabSheetInSplitPanel;
    private HorizontalSplitPanel panel = new HorizontalSplitPanel();

    @Override
    protected void setup(VaadinRequest request) {
        panel.setHeight("200px");
        tabSheetInSplitPanel = new TabSheet();
        tabSheetInSplitPanel.setWidth(100, Unit.PERCENTAGE);
        for (int i = 0; i < 100; i++) {
            tabSheetInSplitPanel.addTab(new Label("Tab " + i), "Tab " + i);
        }

        Layout buttonLayout = new VerticalLayout();

        buttonLayout
                .addComponent(new Button("Hide TabSheet", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSplitPosition(100, Unit.PERCENTAGE);
                        panel.removeComponent(tabSheetInSplitPanel);
                    }
                }));

        Button showLast = new Button("Show TabSheet and select last tab",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSecondComponent(tabSheetInSplitPanel);
                        panel.setSplitPosition(250, Unit.PIXELS);
                        tabSheetInSplitPanel.setSelectedTab(
                                tabSheetInSplitPanel.getComponentCount() - 1);
                    }
                });
        showLast.setId(BTN_SELECT_LAST_TAB);
        buttonLayout.addComponent(showLast);

        buttonLayout.addComponent(new Button(
                "Show TabSheet and select first tab", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSecondComponent(tabSheetInSplitPanel);
                        panel.setSplitPosition(250, Unit.PIXELS);
                        tabSheetInSplitPanel.setSelectedTab(0);
                    }
                }));

        panel.setFirstComponent(buttonLayout);
        panel.setSplitPosition(100, Unit.PERCENTAGE);
        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking \"Show TabSheet and select last tab\" should scroll to the last tab and not disable tab scrolling.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 20052;
    }
}
