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
package com.vaadin.tests.components.ui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * UI test for TextArea behavior when ENTER has been assigned as a keyboard
 * shortcut.
 * 
 * @author Vaadin Ltd
 */
public class TextAreaEventPropagation extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        FormLayout form = new FormLayout();
        TextArea textArea = new TextArea("Text input");
        TextField textField = new TextField("Text field input");

        Button enterButton = new Button("Enter");
        enterButton.setClickShortcut(KeyCode.ENTER);
        enterButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                log("Enter button pressed");
            }
        });

        Button shiftEnterButton = new Button("Shift-Enter");
        shiftEnterButton.setClickShortcut(KeyCode.ENTER, ModifierKey.SHIFT);
        shiftEnterButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                log("Shift-Enter button pressed");
            }
        });

        Button ctrlEnterButton = new Button("Ctrl-Enter");
        ctrlEnterButton.setClickShortcut(KeyCode.ENTER, ModifierKey.CTRL);
        ctrlEnterButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                log("Ctrl-Enter button pressed");
            }
        });

        Button escapeButton = new Button("Escape");
        escapeButton.setClickShortcut(KeyCode.ESCAPE);
        escapeButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Escape button pressed");
            }
        });

        form.addComponent(textArea);
        form.addComponent(textField);
        form.addComponent(enterButton);
        form.addComponent(escapeButton);
        form.addComponent(shiftEnterButton);
        form.addComponent(ctrlEnterButton);
        addComponent(form);

    }

    @Override
    protected String getTestDescription() {
        return "Enter as s shortcut in a TextArea should not trigger shortcuts as enter is handled internally. Enter + modifier key should trigger shortcut.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12424);
    }

}
