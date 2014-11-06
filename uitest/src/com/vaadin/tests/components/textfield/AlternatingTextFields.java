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
package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * When two TextFields repeatedly disable each other, ensure that also their
 * input prompts are removed
 * 
 * @since
 * @author Vaadin Ltd
 */
public class AlternatingTextFields extends AbstractTestUI {

    public static final String FIRST_TEXTFIELD_INPUT_PROMPT = "Enter first data here";
    public static final String SECOND_TEXTFIELD_INPUT_PROMPT = "Enter second data here";

    @Override
    protected void setup(final VaadinRequest request) {

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField firstTextField = createTextField("First",
                FIRST_TEXTFIELD_INPUT_PROMPT);

        final TextField secondTextField = createTextField("Second",
                SECOND_TEXTFIELD_INPUT_PROMPT);

        addTextChangeListener(firstTextField, secondTextField);
        addTextChangeListener(secondTextField, firstTextField);

        layout.addComponent(firstTextField);
        layout.addComponent(secondTextField);

        addComponent(layout);
    }

    private static TextField createTextField(String number, String inputPrompt) {

        String caption = " TextField with TextChangeListener";

        TextField textField = new TextField(number + caption);
        textField.setImmediate(true);
        textField.setInputPrompt(inputPrompt);

        return textField;
    }

    private void addTextChangeListener(TextField currentTextField,
            final TextField otherTextField) {

        final String otherDefaultPrompt = otherTextField.getInputPrompt();
        currentTextField.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {

                String currentText = event.getText();

                if (currentText.isEmpty() || currentText == null) {
                    // change other to default

                    otherTextField.setInputPrompt(otherDefaultPrompt);
                    otherTextField.setEnabled(true);
                } else {
                    // change other to empty

                    otherTextField.setInputPrompt(null);
                    otherTextField.setEnabled(false);
                }

            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "When two TextFields repeatedly disable each other, ensure that also their input prompts are removed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15144;
    }
}
