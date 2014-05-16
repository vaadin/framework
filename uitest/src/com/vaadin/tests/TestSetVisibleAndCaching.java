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

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class TestSetVisibleAndCaching extends
        com.vaadin.server.LegacyApplication {

    Panel panelA = new Panel("Panel A");
    Panel panelB = new Panel("Panel B");
    Panel panelC = new Panel("Panel C");

    Button buttonNextPanel = new Button("Show next panel");

    int selectedPanel = 0;

    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow(
                "TestSetVisibleAndCaching");
        setMainWindow(mainWindow);

        panelA.setContent(wrapInPanelLayout(new Label(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
        panelB.setContent(wrapInPanelLayout(new Label(
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")));
        panelC.setContent(wrapInPanelLayout(new Label(
                "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC")));

        mainWindow
                .addComponent(new Label(
                        "Inspect transfered data from server to "
                                + "client using firebug (http request / response cycles)."
                                + " See how widgets are re-used,"
                                + " after each panel is once shown in GUI then"
                                + " their contents are not resend."));
        mainWindow.addComponent(buttonNextPanel);
        mainWindow.addComponent(panelA);
        mainWindow.addComponent(panelB);
        mainWindow.addComponent(panelC);

        selectPanel(selectedPanel);

        buttonNextPanel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                selectedPanel++;
                if (selectedPanel > 2) {
                    selectedPanel = 0;
                }
                selectPanel(selectedPanel);
            }
        });

    }

    private VerticalLayout wrapInPanelLayout(Component component) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(component);
        return layout;
    }

    private void selectPanel(int selectedPanel) {
        System.err.println("Selecting panel " + selectedPanel);
        switch (selectedPanel) {
        case 0:
            panelA.setVisible(true);
            panelB.setVisible(false);
            panelC.setVisible(false);
            break;
        case 1:
            panelA.setVisible(false);
            panelB.setVisible(true);
            panelC.setVisible(false);
            break;
        case 2:
            panelA.setVisible(false);
            panelB.setVisible(false);
            panelC.setVisible(true);
            break;
        }
    }
}
