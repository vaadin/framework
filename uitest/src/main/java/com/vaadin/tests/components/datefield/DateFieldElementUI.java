package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.DateField;

public class DateFieldElementUI extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new DateField());
        addComponent(new TestDateField());
    }

    @Override
    protected Integer getTicketNumber() {
        return 17090;
    }

    @Override
    protected String getTestDescription() {
        return "DateFieldElement should be accessible using TB4 DateFieldElement.";
    }
}
