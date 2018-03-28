package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.VerticalLayout;

public class AriaDisabled extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        final AbstractLocalDateField disabledDateField = new TestDateField(
                "Disabled DateField");
        disabledDateField.setEnabled(false);

        setContent(content);
        content.addComponent(disabledDateField);
        content.addComponent(new TestDateField("Enabled DateField"));
    }

    @Override
    protected String getTestDescription() {
        return "Test for aria-disabled attribute on DateField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13463;
    }
}
