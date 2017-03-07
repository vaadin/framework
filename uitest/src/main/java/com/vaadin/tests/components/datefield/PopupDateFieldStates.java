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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;

@SuppressWarnings("serial")
public class PopupDateFieldStates extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(createPopupDateField(true, true));
        gridLayout.addComponent(createPopupDateField(true, false));
        gridLayout.addComponent(createPopupDateField(false, true));
        gridLayout.addComponent(createPopupDateField(false, false));

        getLayout().addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Test that PopupDateField is rendered consistently across browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14565;
    }

    private static DateField createPopupDateField(final boolean enabled,
            final boolean textFieldEnabled) {
        final DateField popupDatefield = new DateField();

        popupDatefield.setValue(LocalDate.of(2014, 9, 3));
        popupDatefield.setCaption("Enabled: " + enabled
                + ", Text field enabled: " + textFieldEnabled);
        popupDatefield.setEnabled(enabled);
        popupDatefield.setTextFieldEnabled(textFieldEnabled);
        return popupDatefield;
    }

}
