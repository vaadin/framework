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
package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.MenuBar;

public class MenuBarTooltip extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menubar = new MenuBar();

        MenuBar.MenuItem menuitem = menubar.addItem("Menu item", null, null);
        menuitem.setDescription("Menu item description");

        MenuBar.MenuItem submenuitem1 = menuitem.addItem("Submenu item 1", null,
                null);
        submenuitem1.setDescription("Submenu item 1 description");

        MenuBar.MenuItem submenuitem2 = menuitem.addItem("Submenu item 2", null,
                null);
        submenuitem2.setDescription("Submenu item 2 description");

        addComponent(menubar);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14854;
    }

    @Override
    protected String getTestDescription() {
        return "MenuItem tooltip should have a larger z-index than MenuBar/MenuItem.";
    }
}
