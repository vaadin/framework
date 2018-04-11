package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.PasswordField;

@Widgetset(TestingWidgetSet.NAME)
public class CapsLockWarningUI extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        PasswordField field = new PasswordField("Enter your password");
        CapsLockWarning.warnFor(field);

        addComponent(field);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial code for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20component%20extension";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
