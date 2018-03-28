package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.InlineDateField;

public class DisabledInlineDateField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractLocalDateField df = new InlineDateField("Disabled");
        LocalDate date = LocalDate.of(2014, 6, 5);
        df.setValue(date);
        df.setEnabled(false);
        addComponent(df);

        df = new InlineDateField("Read-only");
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
