package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;

public class DisabledDateFieldPopup extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractLocalDateField field = new TestDateField();
        field.setEnabled(false);
        addComponent(field);
    }

    @Override
    protected String getTestDescription() {
        return "Don't open popup calendar if datefield is disabled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13508;
    }

}
