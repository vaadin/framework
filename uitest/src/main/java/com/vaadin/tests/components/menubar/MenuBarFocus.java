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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarFocus extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final MenuBar bar = buildMenu();
        Button focusButton = buildButton(bar);

        addComponent(bar);
        addComponent(focusButton);
        getLayout().setSpacing(true);
    }

    private MenuBar buildMenu() {
        final MenuBar bar = new MenuBar();
        bar.setDescription("Root Menu");

        Command command = new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                addComponent(new Label(selectedItem.getText() + " clicked"));

            }
        };

        // File
        final MenuItem file = bar.addItem("File", null);
        file.addItem("Foo", command);
        file.addItem("Bar", command);

        // Edit
        MenuItem edit = bar.addItem("Edit", null);
        edit.addItem("Baz", command);
        edit.addItem("Bay", command);

        bar.setTabIndex(2);
        return bar;
    }

    private Button buildButton(final MenuBar bar) {
        ClickListener buttonClickListener = new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                bar.focus();
            }
        };

        Button focusButton = new Button("Click me to focus the menubar",
                buttonClickListener);
        focusButton.setTabIndex(1);
        return focusButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "This test checks if you can focus a menu bar on the client from the server side";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7674;
    }

}
