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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarNavigation extends AbstractTestUIWithLog
        implements Command {

    private MenuItem edit;
    private MenuItem file;
    private MenuItem export;

    @Override
    protected String getTestDescription() {
        return "Test case for mouse and keyboard navigation in MenuBar";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5174;
    }

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar mb = new MenuBar();
        file = mb.addItem("File", null);
        file.addItem("Open", this);
        file.addItem("Save", this);
        file.addItem("Save As..", this);
        file.addSeparator();
        export = file.addItem("Export..", null);
        export.addItem("As PDF...", this);
        file.addSeparator();
        file.addItem("Exit", this);
        edit = mb.addItem("Edit", null);
        edit.addItem("Copy", this);
        edit.addItem("Cut", this);
        edit.addItem("Paste", this);
        mb.addItem("Help", this);

        addComponent(mb);
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        log("MenuItem " + getName(selectedItem) + " selected");
    }

    private String getName(MenuItem selectedItem) {
        String name = "";
        if (selectedItem.getParent() != null) {
            name = getName(selectedItem.getParent()) + "/";
        }
        return name + selectedItem.getText();
    }
}
