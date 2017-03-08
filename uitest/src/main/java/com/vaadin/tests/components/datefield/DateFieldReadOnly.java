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

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.Button;

public class DateFieldReadOnly extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "A read-only DateField should not show the popup button and not be editable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3163;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final AbstractLocalDateField timeField = new TestDateField(
                "A read-only datefield");
        timeField.setCaption(null);
        timeField.setIcon(null);
        timeField.setWidth("8em");
        timeField.addStyleName("timeField");
        timeField.setLocale(new Locale("fi"));

        // Set date so that testing always has same time
        timeField.setValue(LocalDate.of(2009, 6, 12));
        timeField.setReadOnly(true);

        addComponent(timeField);

        Button b = new Button("Switch read-only");
        b.addClickListener(
                event -> timeField.setReadOnly(!timeField.isReadOnly()));

        addComponent(b);
    }
}
