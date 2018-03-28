package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.InlineDateTimeField;

public class DisabledInlineDateTimeField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        InlineDateTimeField df = new InlineDateTimeField("Disabled");
        LocalDateTime date = LocalDateTime.of(2014, 6, 5, 11, 34);
        df.setValue(date);
        df.setEnabled(false);
        addComponent(df);

        df = new InlineDateTimeField("Read-only");
        df.setValue(date);
        df.setReadOnly(true);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "Testing disabled and read-only modes of InlineDateField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10262;
    }

}
