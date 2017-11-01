package com.vaadin.tests.components.notification;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Notification;

public class ChromeBottomNotification extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        addButton("Show notification", event -> {
            Notification notification = new Notification("Hello world",
                    Notification.Type.ERROR_MESSAGE);
            notification.setPosition(Position.BOTTOM_CENTER);
            notification.show(getPage());
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 17252;
    }

    @Override
    protected String getTestDescription() {
        return "Bottom notification on Chrome goes up to top";
    }
}
