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
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.GridLayout;

/**
 * @author Vaadin Ltd
 *
 */
public class PopupDateTimeFieldStates extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        final GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(createPopupDateTimeField(true, true));
        gridLayout.addComponent(createPopupDateTimeField(true, false));
        gridLayout.addComponent(createPopupDateTimeField(false, true));
        gridLayout.addComponent(createPopupDateTimeField(false, false));

        getLayout().addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Test that PopupDateTimeField is rendered consistently across browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14565;
    }

    private static DateTimeField createPopupDateTimeField(final boolean enabled,
            final boolean textFieldEnabled) {
        final DateTimeField popupDatefield = new DateTimeField();

        popupDatefield.setValue(LocalDateTime.of(2014, 9, 3, 10, 34));
        popupDatefield.setCaption("Enabled: " + enabled
                + ", Text field enabled: " + textFieldEnabled);
        popupDatefield.setEnabled(enabled);
        popupDatefield.setTextFieldEnabled(textFieldEnabled);
        return popupDatefield;
    }

}
