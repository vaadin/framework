package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Label;

public class DateFieldDayResolutionOffset extends AbstractReindeerTestUI {

    private final String initialDateString = "09/01/2014 00:00:00";

    @Override
    protected void setup(VaadinRequest request) {
        final Label dateValue = new Label(initialDateString);
        dateValue.setId("dateValue");

        final DateTimeFormatter dateformat = getDateFormat();
        final DateTimeField dateField = getDateField(dateformat);

        addComponent(dateValue);
        addComponent(dateField);

        dateField.addValueChangeListener(event -> dateValue
                .setValue(dateformat.format(dateField.getValue())));
    }

    private DateTimeField getDateField(DateTimeFormatter dateformat) {
        final DateTimeField dateField = new DateTimeField();
        LocalDateTime initialDate = dateformat.parse(initialDateString,
                LocalDateTime::from);
        dateField.setResolution(DateTimeResolution.DAY);
        dateField.setValue(initialDate);
        return dateField;
    }

    private DateTimeFormatter getDateFormat() {
        final DateTimeFormatter dateformat = DateTimeFormatter
                .ofPattern("MM/dd/yyyy HH:mm:ss");
        return dateformat;
    }

    @Override
    protected String getTestDescription() {
        return "The time should stay at 00:00:00 when selecting dates with Resolution.DAY selected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14653;
    }
}
