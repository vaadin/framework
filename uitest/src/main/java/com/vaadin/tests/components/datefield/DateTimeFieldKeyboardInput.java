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
