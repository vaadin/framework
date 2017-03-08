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
package com.vaadin.tests.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

public class DisableEnableCascade extends TestBase {

    private Panel outerPanel;
    private TabSheet innerTabsheet;
    private Button button;
    private Button enableDisablePanelButton;
    private Button enableDisableTabSheetButton;
    private Button enableDisableButtonButton;

    @Override
    protected void setup() {

        outerPanel = new Panel("Outer panel, enabled");
        innerTabsheet = new TabSheet();
        innerTabsheet.setCaption("Inner Tabsheet, enabled");

        button = new Button("Button, enabled");

        outerPanel.setContent(innerTabsheet);
        innerTabsheet.addTab(button, "Tab containing button");

        addComponent(outerPanel);

        enableDisablePanelButton = new Button("Disable panel",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(outerPanel, enableDisablePanelButton);

                    }
                });

        enableDisableTabSheetButton = new Button("Disable TabSheet",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(innerTabsheet,
                                enableDisableTabSheetButton);

                    }
                });

        enableDisableButtonButton = new Button("Disable Button",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(button, enableDisableButtonButton);

                    }
                });

        addComponent(enableDisablePanelButton);
        addComponent(enableDisableTabSheetButton);
        addComponent(enableDisableButtonButton);
    }

    protected void enableDisable(Component target, Button button) {
        if (target.isEnabled()) {
            target.setEnabled(false);
            button.setCaption(button.getCaption().replace("Disable", "Enable"));
            target.setCaption(
                    target.getCaption().replace("enabled", "disabled"));
        } else {
            target.setEnabled(true);
            button.setCaption(button.getCaption().replace("Enable", "Disable"));
            target.setCaption(
                    target.getCaption().replace("disabled", "enabled"));
        }
    }

    @Override
    protected String getDescription() {
        return "Tests the disable state is cascaded correctly to children. Disabling a parent should disabled its children aswell. The buttons only toggle the state of the target component.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8507;
    }

}
