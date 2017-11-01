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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextFieldEmptyingPrompt extends AbstractReindeerTestUI {

    final TextField textField = new TextField();
    final Label label = new Label();
    final static String RANDOM_PROMPT = "Some prompt here";

    @Override
    public String getTestDescription() {
        return "Type something, then erase it, then click on the button.<br>"
                + "Input prompt should dissapear.<br>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15144;
    }

    @Override
    protected void setup(VaadinRequest request) {

        addComponent(label);

        textField.setPlaceholder(RANDOM_PROMPT);
        textField.addValueChangeListener(listener -> label
                .setValue("Textfield value: " + listener.getValue()));
        addComponent(textField);

        Button button = new Button("Click To Remove Prompt");
        button.addClickListener(event -> textField.setPlaceholder(""));
        addComponent(button);
    }
}
