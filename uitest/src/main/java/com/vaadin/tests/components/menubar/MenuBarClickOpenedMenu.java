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
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Test UI for top click on expanded top level menu and sub-menus.
 *
 * @author Vaadin Ltd
 */
public class MenuBarClickOpenedMenu extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName("top-level");
        MenuItem file = menuBar.addItem("File", null);
        file.setStyleName("first-level");
        MenuItem open = file.addItem("Open", null);
        open.setStyleName("second-level");
        MenuItem as = open.addItem("as", null);
        as.setStyleName("third-level");
        MenuItem leaf = as.addItem("Text", new MenuBarCommand());
        leaf.setStyleName("leaf");
        addComponent(menuBar);
    }

    @Override
    protected String getTestDescription() {
        return "Top level menu item should always close menu on click. "
                + "Submenu should not close if it's already opened";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14568;
    }

    private class MenuBarCommand implements Command {
        @Override
        public void menuSelected(MenuItem selectedItem) {
        }
    }

}
