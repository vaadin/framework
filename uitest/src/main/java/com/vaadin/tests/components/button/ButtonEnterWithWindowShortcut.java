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
package com.vaadin.tests.components.button;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ButtonEnterWithWindowShortcut extends TestBase {
    Log log = new Log(5);

    @Override
    protected void setup() {
        getMainWindow().addActionHandler(new Handler() {
            private static final long serialVersionUID = -4976129418325394913L;

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                log.log(action.getCaption() + " pressed in window");
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                ShortcutAction enter = new ShortcutAction("enter",
                        ShortcutAction.KeyCode.ENTER, null);
                ShortcutAction space = new ShortcutAction("space",
                        ShortcutAction.KeyCode.SPACEBAR, null);
                return new Action[] { enter, space };
            }
        });

        Button button = new Button("Focus me and press enter",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("button click listener fired");
                    }
                });
        button.focus();

        addComponent(log);
        addComponent(button);
    }

    @Override
    protected String getDescription() {
        return "Pressing enter or space with the button focused should trigger the button click listener and not the shortcut action on the window.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(5433);
    }

}
