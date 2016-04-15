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
package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class SpaceMenuBarNavigation extends AbstractTestUI implements Command {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName("menu-bar");

        MenuItem item = menuBar.addItem("menu", null);

        item.addItem("subitem", this);

        addComponent(menuBar);
    }

    @Override
    protected String getTestDescription() {
        return "Space key code should trigger menu actions/submenu";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12525;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        Label label = new Label("action result");
        label.addStyleName("action-result");
        addComponent(label);
    }

}
