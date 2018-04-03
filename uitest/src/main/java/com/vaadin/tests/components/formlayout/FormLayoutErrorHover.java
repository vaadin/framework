package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;

public class FormLayoutErrorHover extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout formLayout = new FormLayout();
        DateField fromDate = new DateField("Date");
        formLayout.addComponent(fromDate);

        addComponent(formLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Enter some random text to the date field and press enter. Then hover the error indicator. This should show a message about the error.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8794);
    }

}
