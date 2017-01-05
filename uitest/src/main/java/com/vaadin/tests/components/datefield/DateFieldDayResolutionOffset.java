package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Label;

public class DateFieldDayResolutionOffset extends AbstractReindeerTestUI {

    private final String initialDateString = "09/01/2014";

    @Override
    protected void setup(VaadinRequest request) {
        final Label dateValue = new Label(initialDateString);
        dateValue.setId("dateValue");

        final DateTimeFormatter dateformat = getDateFormat();
        final AbstractDateField dateField = getDateField(dateformat);

        addComponent(dateValue);
        addComponent(dateField);

        dateField.addValueChangeListener(event -> dateValue
                .setValue(dateformat.format(dateField.getValue())));
    }

    private AbstractDateField getDateField(DateTimeFormatter dateformat) {
        final AbstractDateField dateField = new TestDateField();
        LocalDate initialDate = dateformat.parse(initialDateString,
                LocalDate::from);
        dateField.setResolution(Resolution.DAY);
        dateField.setValue(initialDate);
        return dateField;
    }

    private DateTimeFormatter getDateFormat() {
        final DateTimeFormatter dateformat = DateTimeFormatter
                .ofPattern("MM/dd/yyyy");
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
