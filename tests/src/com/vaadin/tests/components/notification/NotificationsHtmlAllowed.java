package com.vaadin.tests.components.notification;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;

public class NotificationsHtmlAllowed extends TestBase implements ClickListener {

    private TextArea messageField;
    private CheckBox htmlAllowedBox;
    private TextField captionField;

    @Override
    protected void setup() {
        captionField = new TextField("Caption", "Hello <u>world</u>");
        addComponent(captionField);
        messageField = new TextArea("Message",
                "Hello <i>world</i>\nWith a newline <br/>And a html line break");
        messageField.setRows(10);
        addComponent(messageField);
        htmlAllowedBox = new CheckBox("Html content allowed", true);
        addComponent(htmlAllowedBox);
        Button showNotification = new Button("Show notification", this);
        addComponent(showNotification);
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
        Notification n = new Notification((String) captionField.getValue(),
                (String) messageField.getValue(),
                Notification.TYPE_HUMANIZED_MESSAGE,
                htmlAllowedBox.booleanValue());
        event.getButton().getWindow().showNotification(n);

    }
}
