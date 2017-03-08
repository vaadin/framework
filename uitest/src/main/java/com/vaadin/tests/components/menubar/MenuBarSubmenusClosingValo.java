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
package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarSubmenusClosingValo extends AbstractTestUI {

    private MenuItem edit;
    private MenuItem file;
    private MenuItem help;

    @Override
    protected String getTestDescription() {
        return "Tests that when moving mouse fast over menu items "
                + "previous submenu popup closes before new submenu popup opens";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15255;
    }

    @Override
    protected void setup(VaadinRequest request) {
        // here we increase animation time to 1 second for to do auto testing
        // possible
        getPage().getStyles()
                .add(".valo .v-menubar-popup[class*=\"animate-in\"] {"
                        + "-webkit-animation: valo-overlay-animate-in 1000ms; "
                        + "-moz-animation: valo-overlay-animate-in 1000ms; "
                        + "animation: valo-overlay-animate-in 1000ms;};");

        getPage().getStyles()
                .add(".valo .v-menubar-popup[class*=\"animate-out\"] {"
                        + "-webkit-animation: valo-animate-out-fade 1000ms; "
                        + "-moz-animation: valo-animate-out-fade 1000ms; "
                        + "animation: valo-animate-out-fade 1000ms;};");

        MenuBar mb = new MenuBar();
        file = mb.addItem("File", null);
        file.addItem("File1", null);
        file.addItem("File2", null);
        file.addItem("File3", null);
        edit = mb.addItem("Edit", null);
        edit.addItem("Edit1", null);
        edit.addItem("Edit2", null);
        edit.addItem("Edit3", null);
        help = mb.addItem("Help", null);
        help.addItem("Help1", null);
        help.addItem("Help2", null);
        MenuItem helpMenuItem = help.addItem("Help3", null);
        helpMenuItem.addItem("SubHelp3", null);

        addComponent(mb);
    }
}
