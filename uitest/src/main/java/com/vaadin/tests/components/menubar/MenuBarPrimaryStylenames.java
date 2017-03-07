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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarPrimaryStylenames extends TestBase {

    @Override
    protected void setup() {
        final MenuBar mainMenu = new MenuBar();
        mainMenu.setPrimaryStyleName("my-menu-bar");

        MenuItem submenu1 = mainMenu.addItem("Submenu1", null);
        submenu1.setStyleName("normal icon-white icon-headphones");

        MenuItem item1 = submenu1.addItem("Item1", null);
        item1.setCheckable(true);
        item1.setStyleName("my-menu-item");
        submenu1.addItem("Item2", null);

        MenuItem submenu2 = mainMenu.addItem("Submenu2", null);
        MenuItem menu1 = submenu2.addItem("Menu1", null);
        menu1.addItem("Item11", null);

        addComponent(mainMenu);

        addComponent(new Button("Change primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        mainMenu.setPrimaryStyleName("my-other-menu");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "Menubar should support primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9908;
    }

}
