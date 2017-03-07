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
package com.vaadin.tests.components.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.TextField;

public class WindowShouldRemoveActionHandler extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "Adding action handlers to the window should make them appear on the client side. Removing the action handlers should remove them also from the client side, also if all action handlers are removed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2941;
    }

    private Label state;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setMargin(new MarginInfo(true, false, false, false));
        state = new Label("An UI with no action handlers.");
        state.setId("state");
        addComponents(state, new TextField());

        addButton("Add an action handler", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                add();
            }

        });
        addButton("Add another action handler", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addAnother();
            }

        });
        addButton("Remove an action handler", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                remove();
            }

        });
    }

    public void remove() {
        state.setValue(state.getValue() + " - Removed handler");
        removeActionHandler(actionHandlers.remove(actionHandlers.size() - 1));
    }

    private List<Handler> actionHandlers = new ArrayList<>();

    public void add() {
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
                Notification.show("Handling action " + action.getCaption());
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
                Notification.show("Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    private void addHandler(Handler actionHandler) {
        actionHandlers.add(actionHandler);
        addActionHandler(actionHandler);
        state.setValue(
                "An UI with " + actionHandlers.size() + " action handlers");

    }
}
