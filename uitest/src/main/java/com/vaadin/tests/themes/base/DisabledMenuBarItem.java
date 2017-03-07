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
package com.vaadin.tests.themes.base;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.MenuBar;

public class DisabledMenuBarItem extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menubar = new MenuBar();

        MenuBar.MenuItem item = menubar.addItem("Item", null);
        item.setEnabled(false);
        item.setIcon(new ThemeResource("common/icons/error.png"));

        addComponent(menubar);
    }

    @Override
    protected String getTestDescription() {
        return "Image icon should be greyed out.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15381;
    }
}
