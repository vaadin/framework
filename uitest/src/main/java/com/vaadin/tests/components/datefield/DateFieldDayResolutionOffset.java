package com.vaadin.tests.components.datefield;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFieldDayResolutionOffset extends AbstractTestUI {

    private final String initialDateString = "09/01/2014 00:00:00";

    @Override
    protected void setup(VaadinRequest request) {
        final Label dateValue = new Label(initialDateString);
        dateValue.setId("dateValue");

        final TimeZone timezone = TimeZone.getTimeZone("GMT");
        final SimpleDateFormat dateformat = getDateFormat(timezone);
        final DateField dateField = getDateField(timezone, dateformat);

        addComponent(dateValue);
        addComponent(dateField);

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                dateValue.setValue(dateformat.format(dateField.getValue()));
            }
        });
    }

    private DateField getDateField(TimeZone timezone,
            SimpleDateFormat dateformat) {
        final DateField dateField = new DateField();
        try {
            Date initialDate = dateformat.parse(initialDateString);
            dateField.setResolution(Resolution.DAY);
            dateField.setTimeZone(timezone);
            dateField.setValue(initialDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateField;
    }

    private SimpleDateFormat getDateFormat(TimeZone timezone) {
        final SimpleDateFormat dateformat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss");
        dateformat.setTimeZone(timezone);
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
