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

import java.time.LocalDateTime;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Label;

public class DateTimeFieldKeyboardInput extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final DateTimeField dateField = new DateTimeField("Select date",
                LocalDateTime.of(2014, 1, 15, 7, 2));
        dateField.setDateFormat("dd.MM.yyyy HH:mm");
        addComponent(dateField);
        dateField.addValueChangeListener(
                event -> addComponent(new Label("Date has been changed.")));
    }

    @Override
    public Integer getTicketNumber() {
        return 16677;
    }

    @Override
    public String getTestDescription() {
        return "When a new date is entered in the text field using the keyboard, pressing the return key after typing the date, "
                + "a label with the text 'Date has been changed' should appear.";
    }
}