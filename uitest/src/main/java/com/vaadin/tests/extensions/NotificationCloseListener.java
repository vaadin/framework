package com.vaadin.tests.extensions;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;

/**
 * UI used to validate Notification closes works.
 */
public class NotificationCloseListener extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBox checkBox = new CheckBox();
        addComponent(checkBox);

        Notification notification = Notification.show("something");
        notification.addCloseListener(event -> checkBox.setValue(true));
    }

    @Override
    protected String getTestDescription() {
        return "Notification Close listener is called.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10027;
    }

}
