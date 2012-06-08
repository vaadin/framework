/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.formlayout;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;

public class FormLayoutErrorHover extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        FormLayout formLayout = new FormLayout();
        PopupDateField fromDate = new PopupDateField("Date");
        fromDate.setImmediate(true);
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
