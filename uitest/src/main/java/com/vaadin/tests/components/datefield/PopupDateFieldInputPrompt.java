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
package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

/**
 * Tests that state change doesn't set input prompt back to PopupDateField if
 * focus is still in the input field.
 *
 * @author Vaadin Ltd
 */
public class PopupDateFieldInputPrompt extends AbstractReindeerTestUI {

    private TextField text = new TextField("TextField");
    private DateField dateField = new DateField();

    @Override
    protected void setup(VaadinRequest request) {

        text.addValueChangeListener(listener -> {
            // update PopupDateField's state
            dateField.setRequired(!dateField.isRequired());
        });

        dateField.setInputPrompt("prompt");
        dateField.setCaption("PopupDateField");

        addComponent(text);
        addComponent(dateField);
    }

    @Override
    protected String getTestDescription() {
        return "Write something to the TextField and use tabulator to move to PopupDateField."
                + "<br>PopupDateField shouldn't get input prompt back before focus leaves the input field,"
                + "<br>even if TextField's value change updates PopupDateField's state.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18027;
    }

}
