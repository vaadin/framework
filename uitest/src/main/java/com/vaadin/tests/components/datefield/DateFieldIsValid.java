package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateField;

public class DateFieldIsValid extends AbstractTestUIWithLog {

    @Override
    protected String getTestDescription() {
        return "A dateField with invalid text should return false in isValid both when "
                + "handling ValueChange event and after value is changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14487;
    }

    private String pattern = "dd/MM/yy";
    private SimpleDateFormat format = new SimpleDateFormat(pattern);

    @Override
    protected void setup(VaadinRequest request) {
        final DateField dateField = new DateField("Insert Date: ");
        dateField.setImmediate(true);
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

    /**
     * @since
     * @param value
     * @return
     */
    protected String format(Date value) {
        if (value != null) {
            return format.format(value);
        } else {
            return null;
        }
    }
}
