package com.vaadin.tests.components.datefield;

import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;

public class PopupDateFieldConnector extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new LegacyPopupDateField());
        addComponent(new LegacyDateField());
    }

    @Override
    protected Integer getTicketNumber() {
        return 17090;
    }

    @Override
    protected String getTestDescription() {
        return "PopupDateFieldElement should be accessible using TB4 PopupDateFieldElement.";
    }
}
