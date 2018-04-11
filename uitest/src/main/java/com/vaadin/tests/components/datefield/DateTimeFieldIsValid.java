package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateTimeField;

public class DateTimeFieldIsValid extends AbstractTestUIWithLog {

    @Override
    protected String getTestDescription() {
        return "A dateField with invalid text should return false in isValid both when "
                + "handling ValueChange event and after value is changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14487;
    }

    private String pattern = "dd/MM/yy H.mm";
    private DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);

    @Override
    protected void setup(VaadinRequest request) {
        final DateTimeField dateField = new DateTimeField("Insert Date: ");
        dateField.setDateFormat(pattern);

        dateField.addValueChangeListener(event -> log("valueChange: value: "
                + format(dateField.getValue()) + ", is valid: "
                + (dateField.getErrorMessage() == null)));
        addComponent(dateField);
        addButton("check dateField",
                event -> log("buttonClick: value: "
                        + format(dateField.getValue()) + ", is valid: "
                        + (dateField.getErrorMessage() == null)));
    }

    protected String format(LocalDateTime value) {
        if (value != null) {
            return format.format(value);
        } else {
            return null;
        }
    }
}
