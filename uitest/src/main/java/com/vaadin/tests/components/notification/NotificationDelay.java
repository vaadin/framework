package com.vaadin.tests.components.notification;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * UI for notification delay test.
 *
 * @author Vaadin Ltd
 */
public class NotificationDelay extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Notification notification = new Notification("Foo",
                Type.HUMANIZED_MESSAGE);
        notification.setDelayMsec(500);
        notification.show(getPage());
    }

    @Override
    protected String getTestDescription() {
        return "Notification should be closed after delay";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14689;
    }

}
