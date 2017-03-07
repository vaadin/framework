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
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarRootItemSelectWithKeyboard extends TestBase {

    @Override
    protected void setup() {
        Command c = new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getMainWindow().showNotification(selectedItem.getText());

            }
        };

        MenuBar root = new MenuBar();

        MenuItem submenu = root.addItem("Hello", null);
        submenu.addItem("World", c);

        root.addItem("World", c);
        addComponent(root);
    }

    @Override
    protected String getDescription() {
        return "When selecting an root menu item from the menubar with the keyboard (enter) the selection should be removed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5180;
    }

}
