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
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * UI test for TextArea behavior when ENTER has been assigned as a keyboard
 * shortcut.
 * 
 * @author Vaadin Ltd
 */
public class TextAreaEventPropagation extends AbstractTestUIWithLog {

    protected static final String BUTTON_PRESSED = "Button Pressed";

    protected static final String NO_BUTTON_PRESSED = "No Button Pressed";

    private Label enterButtonPressed;

    private Label escapeButtonPressed;

    @Override
    protected void setup(VaadinRequest request) {

        FormLayout form = new FormLayout();
        TextArea textArea = new TextArea("Text input");
        TextField textField = new TextField("Text field input");
        enterButtonPressed = new Label(NO_BUTTON_PRESSED);
        enterButtonPressed.setCaption("Enter Label");
        escapeButtonPressed = new Label(NO_BUTTON_PRESSED);
        escapeButtonPressed.setCaption("Escape Label");

        Button enterButton = new Button("Enter");
        enterButton.setClickShortcut(KeyCode.ENTER);
        enterButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                enterButtonPressed.setValue(BUTTON_PRESSED);
            }
        });

        Button escapeButton = new Button("Escape");
        escapeButton.setClickShortcut(KeyCode.ESCAPE);
        escapeButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                escapeButtonPressed.setValue(BUTTON_PRESSED);
            }
        });

        form.addComponent(textArea);
        form.addComponent(textField);
        form.addComponent(enterButton);
        form.addComponent(escapeButton);
        form.addComponent(enterButtonPressed);
        form.addComponent(escapeButtonPressed);
        addComponent(form);

    }

    @Override
    protected String getTestDescription() {
        return "Currently if enter key is set as a shortcut for some component, it won't be possible for the user to enter newline in a textarea.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12424);
    }

}
