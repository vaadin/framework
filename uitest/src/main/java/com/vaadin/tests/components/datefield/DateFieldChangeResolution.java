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
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class DateFieldChangeResolution extends AbstractReindeerTestUI {

    public static final String DATEFIELD_ID = "datefield";
    // The ID of a button is BUTTON_BASE_ID + resolution, e.g. button-month
    public static final String BUTTON_BASE_ID = "button-";

    @Override
    protected void setup(VaadinRequest request) {
        final AbstractLocalDateField dateField = new DateField("Enter date");
        dateField.setResolution(DateResolution.YEAR);
        dateField.setId(DATEFIELD_ID);
        addComponent(dateField);

        Label l = new Label("Select resolution");
        addComponent(l);
        HorizontalLayout hlayout = new HorizontalLayout();
        addComponent(hlayout);
        for (final DateResolution value : DateResolution.values()) {
            String resolutionString = value.toString().toLowerCase();
            Button button = new Button(resolutionString);
            button.addClickListener(event -> dateField.setResolution(value));
            button.setId(BUTTON_BASE_ID + resolutionString);
            hlayout.addComponent(button);
        }

    }

    @Override
    protected String getTestDescription() {
        return "The calendar should always have the correct resolution and the text field should be empty before selecting a date.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14174;
    }

}
