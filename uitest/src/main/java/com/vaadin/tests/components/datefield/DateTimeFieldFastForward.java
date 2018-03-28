package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.InlineDateTimeField;

public class DateTimeFieldFastForward extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new InlineDateTimeField());
    }

    @Override
    protected String getTestDescription() {
        return "Tests that right-click doesn't interfere with fast-forwarding (holding down left mouse button).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8012;
    }

}
