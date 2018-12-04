package com.vaadin.tests.minitutorials.v7b1;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.PasswordField;

@Widgetset(TestingWidgetSet.NAME)
public class ReducingRoundTrips extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        PasswordField passwordField = new PasswordField("Enter password");
        CapsLockWarningWithRpc capsLockWarningWithRpc = new CapsLockWarningWithRpc();

        capsLockWarningWithRpc.extend(passwordField);

        addComponent(passwordField);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial code for https://vaadin.com/wiki/-/wiki/Main/Reducing%20server%20round%20trips%20for%20components";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
