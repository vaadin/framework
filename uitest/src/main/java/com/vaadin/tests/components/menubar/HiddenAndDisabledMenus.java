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
import com.vaadin.ui.MenuBar.MenuItem;

public class HiddenAndDisabledMenus extends TestBase {

    @Override
    protected void setup() {
        MenuBar mb = new MenuBar();
        mb.addItem("Item 1", null);
        mb.addItem("Item 2 - hidden", null).setVisible(false);
        MenuItem submenu = mb.addItem("Item 3 - sub menu", null);
        mb.addItem("Item 4 - hidden", null).setVisible(false);
        submenu.addItem("Sub item 1 - disabled", null).setEnabled(false);
        submenu.addItem("Sub item 2 - enabled", null);
        submenu.addItem("Sub item 3 - visible", null);
        submenu.addItem("Sub item 4 - hidden", null).setVisible(false);

        addComponent(mb);
    }

    @Override
    protected String getDescription() {
        return "The menu contains 4 items, 2 of which are hidden. The sub menu contains 4 items, the last one is hidden";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4528;
    }

}
