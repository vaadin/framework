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
package com.vaadin.tests.components.textfield;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.ui.TextField;

public class TextFieldInputPromptAndClickShortcut extends TestBase {

    @Override
    protected void setup() {
        final Log log = new Log(5);

        final TextField textField = new TextField();
        Button button = new Button("Show Text", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Field value: " + textField.getValue());
            }
        });
        button.setClickShortcut(KeyCode.ESCAPE);

        final CheckBox inputPromptSelection = new CheckBox("Input prompt");
        inputPromptSelection.addValueChangeListener(event -> {
            if (event.getValue()) {
                textField.setInputPrompt("Input prompt");
            } else {
                textField.setInputPrompt(null);
            }
            log.log("Set input prompt: " + textField.getInputPrompt());
        });

        addComponent(textField);
        addComponent(button);
        addComponent(inputPromptSelection);
        addComponent(log);
    }

    @Override
    protected String getDescription() {
        return "With the input propmpt enabled, enter something into the field, press enter, remove the entered text and press the button. The previous text is still reported as the value. Without the input prompt, the new value is instead reported as blank.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
