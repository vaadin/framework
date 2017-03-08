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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class EnableState extends AbstractTestCase {
    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Helloworld Application");

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        final Panel panel = new Panel("Test", panelLayout);
        final Button button = new Button("ablebutton");
        panelLayout.addComponent(button);

        CheckBox enable = new CheckBox("Toggle button enabled", true);
        enable.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            button.setEnabled(enabled);
            // button.requestRepaint();
        });

        CheckBox caption = new CheckBox("Toggle button caption", true);
        caption.addValueChangeListener(
                event -> button.setCaption(button.getCaption() + "+"));

        CheckBox visible = new CheckBox("Toggle panel visibility", true);
        visible.addValueChangeListener(
                event -> panel.setVisible(event.getValue()));

        CheckBox panelEnable = new CheckBox("Toggle panel enabled", true);
        panelEnable.addValueChangeListener(
                event -> panel.setEnabled(event.getValue()));

        mainWindow.addComponent(enable);
        mainWindow.addComponent(caption);
        mainWindow.addComponent(visible);
        mainWindow.addComponent(panelEnable);
        mainWindow.addComponent(panel);

        setMainWindow(mainWindow);
    }

    @Override
    protected String getDescription() {
        return "This tests the enabled/disabled propagation and that enabled/disabled state is updated"
                + " properly even when the parent is invisible. Disabling the Button while the panel is"
                + " invisible should be reflected on the screen when the panel is set visible"
                + " again.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3609;
    }
}
