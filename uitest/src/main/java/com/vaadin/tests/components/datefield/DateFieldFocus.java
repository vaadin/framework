package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

public class DateFieldFocus extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField();
        dateField.addFocusListener(e -> log("focused"));
        dateField.addBlurListener(e -> log("blurred"));
        addComponent(dateField);

        TextField textField = new TextField();
        textField.setCaption("second");
        addComponent(textField);
    }

    @Override
    protected String getTestDescription() {
        return "DateField should not trigger events when nagivating between sub-components.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1008;
    }

}
