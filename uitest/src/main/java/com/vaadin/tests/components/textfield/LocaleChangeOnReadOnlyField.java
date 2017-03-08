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

import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.TextField;

public class LocaleChangeOnReadOnlyField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField textField = getReadOnlyTextField();
        addComponent(textField);

        Button changeLocaleButton = addLocaleChangeButton(textField);
        addComponent(changeLocaleButton);
    }

    private TextField getReadOnlyTextField() {
        final TextField textField = new TextField();

        textField.setConverter(BigDecimal.class);
        textField.setLocale(Locale.US);
        textField.setValue("1024000");
        textField.setReadOnly(true);

        return textField;
    }

    private Button addLocaleChangeButton(final TextField textField) {
        Button changeLocaleButton = new Button();
        changeLocaleButton.setCaption("Change Locale");
        changeLocaleButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                textField.setLocale(Locale.GERMANY);
            }
        });

        return changeLocaleButton;
    }

    @Override
    protected String getTestDescription() {
        return "Read-only fields throw exception when setting converted value in localeMightHaveChanged()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14400;
    }
}
