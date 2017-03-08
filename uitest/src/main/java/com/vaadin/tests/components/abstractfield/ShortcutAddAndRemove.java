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
package com.vaadin.tests.components.abstractfield;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

public class ShortcutAddAndRemove extends AbstractTestUIWithLog {

    private TextField textField;

    @Override
    protected void setup(VaadinRequest request) {
        final Button logButton = new Button("Log a row (enter shortcut)");
        logButton.setClickShortcut(KeyCode.ENTER);
        logButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Log button was clicked");
            }
        });

        final Button removeShortcut = new Button("Remove shortcut");
        removeShortcut.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logButton.removeClickShortcut();
                logButton.setCaption("Log a row (no shortcut)");
            }
        });
        final Button addShortcut = new Button("Add shortcut");
        addShortcut.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logButton.setClickShortcut(KeyCode.ENTER);
                logButton.setCaption("Log a row (enter shortcut)");
            }
        });
        addComponent(log);
        addComponent(logButton);
        textField = new TextField("Enter key is a shortcut...");
        textField.setWidth("20em");
        addComponent(textField);
        addComponent(removeShortcut);
        addComponent(addShortcut);

    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
