package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class NotificationsCssTest extends VerticalLayout {

    private TestSampler parent;
    private String styleName = null;
    private int debugIdCounter = 0;

    public NotificationsCssTest(TestSampler parent) {
        this.parent = parent;
        parent.registerComponent(this);

        Button humanized = new Button("Humanized message",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        createNotification("A message", "A description",
                                Notification.Type.HUMANIZED_MESSAGE);
                    }
                });
        humanized.setId("notifButt" + debugIdCounter++);
        Button warning = new Button("Warning message",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        createNotification("A message", "A description",
                                Notification.Type.WARNING_MESSAGE);
                    }
                });
        warning.setId("notifButt" + debugIdCounter++);
        Button error = new Button("Error message", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createNotification("A message", "A description",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        error.setId("notifButt" + debugIdCounter++);
        Button tray = new Button("Tray message", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createNotification("A message", "A description",
                        Notification.Type.TRAY_NOTIFICATION);
            }
        });
        tray.setId("notifButt" + debugIdCounter++);

        addComponent(humanized);
        addComponent(warning);
        addComponent(error);
        addComponent(tray);
    }

    private void createNotification(String caption, String message,
            Notification.Type type) {

        Notification notification;

        if (message == null) {
            notification = new Notification(caption, type);
        } else {
            notification = new Notification(caption, message, type);
        }

        if (styleName != null) {
            notification.setStyleName(styleName);
        }

        notification.setDelayMsec(-1);
        notification.show(parent.getUI().getPage());
    }

    @Override
    public void setStyleName(String style) {
        styleName = style;
    }

    @Override
    public void addStyleName(String style) {
        styleName = style;
    }

    @Override
    public void removeStyleName(String style) {
        styleName = null;
    }

}
