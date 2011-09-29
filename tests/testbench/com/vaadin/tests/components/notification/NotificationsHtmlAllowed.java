package com.vaadin.tests.components.notification;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class NotificationsHtmlAllowed extends TestBase implements ClickListener {

    private TextArea messageField;
    private CheckBox htmlAllowedBox;
    private TextField captionField;
    private Window subwindow;
    private CheckBox showInSubwindow;

    @Override
    protected void setup() {
        captionField = new TextField("Caption", "Hello <u>world</u>");
        addComponent(captionField);
        captionField.focus();

        messageField = new TextArea("Message",
                "Hello <i>world</i>\nWith a newline <br/>And a html line break");
        messageField.setRows(10);
        addComponent(messageField);

        htmlAllowedBox = new CheckBox("Html content allowed", true);
        addComponent(htmlAllowedBox);

        showInSubwindow = new CheckBox("Show in subwindow", false);
        addComponent(showInSubwindow);

        Button showNotification = new Button("Show notification", this);
        addComponent(showNotification);

        subwindow = new Window("Sub window");
        subwindow.setPositionX(400);
        subwindow.setPositionY(0);
        getMainWindow().addWindow(subwindow);
    }

    @Override
    protected String getDescription() {
        return "Test case for htmlAllowed in notifications";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6097;
    }

    public void buttonClick(ClickEvent event) {
        Notification n = makeNotification();
        Window window;
        if (showInSubwindow.booleanValue()) {
            window = subwindow;
        } else {
            window = event.getButton().getWindow();
        }
        window.showNotification(n);

    }

    private Notification makeNotification() {
        Notification n = new Notification((String) captionField.getValue(),
                (String) messageField.getValue(),
                Notification.TYPE_HUMANIZED_MESSAGE,
                htmlAllowedBox.booleanValue());
        return n;
    }
}
