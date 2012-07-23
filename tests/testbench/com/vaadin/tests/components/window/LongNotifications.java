package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

public class LongNotifications extends TestBase {
    private final String text = "This is a veeeery large notification in the main window which should definitly not exist at all, in any app. But they finally do in real world applications, no matter what you do. People have small screens and desperatly try to run web apps in their iphones.";

    @Override
    protected String getDescription() {
        return "Notifications should not be wider than the screen.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2632;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");

        Button b = new Button("Show loooong notification",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {

                        getMainWindow().showNotification(
                                text,
                                "This is description for the same notifications."
                                        + text,
                                Notification.TYPE_HUMANIZED_MESSAGE);
                    }
                });
        getLayout().addComponent(b);

        b = new Button("Show notifications", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                getMainWindow().showNotification("Example failed",
                        "This is description for the same notifications.",
                        Notification.TYPE_HUMANIZED_MESSAGE);
            }
        });

        getLayout().addComponent(b);

        b = new Button("Show loooong notification  (error)",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {

                        getMainWindow()
                                .showNotification(
                                        text,
                                        "This is description for the same notifications."
                                                + text,
                                        Notification.TYPE_ERROR_MESSAGE);
                    }
                });
        getLayout().addComponent(b);

        b = new Button("Show notification (error)", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                getMainWindow().showNotification("Example failed",
                        "This is description for the same notifications.",
                        Notification.TYPE_ERROR_MESSAGE);
            }
        });

        getLayout().addComponent(b);

    }
}
