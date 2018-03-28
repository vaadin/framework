package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;

public class DateFieldPopupClosing extends AbstractReindeerTestUI {

    static final String DATEFIELD_ID = "datefield";

    @Override
    protected void setup(VaadinRequest request) {
        final AbstractLocalDateField df = new TestDateField();
        df.setId(DATEFIELD_ID);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "DateField popup should be closed when click on popup button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14857;
    }

}
