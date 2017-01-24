package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.InlineDateTimeField;

public class DateTimeFieldElementUI extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new DateTimeField());
        addComponent(new InlineDateTimeField());
    }

    @Override
    protected Integer getTicketNumber() {
        return 17090;
    }

    @Override
    protected String getTestDescription() {
        return "DateTimeField should be accessible using TB4 DateTimeFieldElement.";
    }
}
