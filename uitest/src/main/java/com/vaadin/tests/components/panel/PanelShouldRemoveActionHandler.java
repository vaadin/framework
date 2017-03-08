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
package com.vaadin.tests.components.panel;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class PanelShouldRemoveActionHandler extends TestBase {

    private Panel panel;

    @Override
    protected String getDescription() {
        return "Adding action handlers to the panel should make them appear on the client side. Removing the action handlers should remove them also from the client side, also if all action handlers are removed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2941;
    }

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        panel = new Panel("A panel", layout);
        layout.addComponent(new TextField());
        Button add = new Button("Add an action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        add();
                    }

                });
        Button addAnother = new Button("Add another action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        addAnother();
                    }

                });
        Button remove = new Button("Remove an action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        remove();
                    }

                });

        addComponent(panel);
        addComponent(add);
        addComponent(addAnother);
        addComponent(remove);
    }

    public void remove() {
        panel.setCaption(panel.getCaption() + " - Removed handler");
        panel.removeActionHandler(
                actionHandlers.remove(actionHandlers.size() - 1));
    }

    private List<Handler> actionHandlers = new ArrayList<>();

    public void add() {
        panel.setCaption(panel.getCaption() + " - Added handler");
        Handler actionHandler = new Handler() {

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new ShortcutAction("Ctrl+Left",
                        ShortcutAction.KeyCode.ARROW_LEFT,
                        new int[] { ModifierKey.CTRL }) };
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                getMainWindow().showNotification(
                        "Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    public void addAnother() {
        Handler actionHandler = new Handler() {

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new ShortcutAction("Ctrl+Right",
                        ShortcutAction.KeyCode.ARROW_RIGHT,
                        new int[] { ModifierKey.CTRL }) };
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                getMainWindow().showNotification(
                        "Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    private void addHandler(Handler actionHandler) {
        actionHandlers.add(actionHandler);
        panel.addActionHandler(actionHandler);
        panel.setCaption(
                "A panel with " + actionHandlers.size() + " action handlers");

    }
}
