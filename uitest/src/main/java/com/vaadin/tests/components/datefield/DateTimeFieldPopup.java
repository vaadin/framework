package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateTimeField;

/**
 * Test UI for testing the functionality of the popup button.
 */
public class DateTimeFieldPopup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        DateTimeField dateTimeField = new DateTimeField();
        dateTimeField.setValue(LocalDateTime.of(1999, 12, 1, 12, 00));

        addComponent(dateTimeField);
    }
}
