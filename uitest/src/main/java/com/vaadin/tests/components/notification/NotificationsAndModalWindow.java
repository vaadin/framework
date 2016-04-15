package com.vaadin.tests.components.notification;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

public class NotificationsAndModalWindow extends TestBase {

    @Override
    protected void setup() {
        getMainWindow().showNotification("Notification 1",
                Notification.TYPE_WARNING_MESSAGE);
        getMainWindow().showNotification("Notification 2",
                Notification.TYPE_WARNING_MESSAGE);

        Button b = new Button("Button");
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Window w = new Window("This is a window");
                w.setModal(true);
                getMainWindow().addWindow(w);
            }
        });
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Press the button when both two notifications are visible to add a modal window to the app. When the modal window is visible, the notifications should disappear normally.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7136;
    }

}
