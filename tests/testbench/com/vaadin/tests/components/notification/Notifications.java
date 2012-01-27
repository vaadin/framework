package com.vaadin.tests.components.notification;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;

public class Notifications extends TestBase implements ClickListener {

    private static final String CAPTION = "CAPTION";
    private TextField tf;
    private NativeSelect type;

    @SuppressWarnings("deprecation")
    @Override
    protected void setup() {
        tf = new TextField("Text", "Hello world");
        tf.setRows(10);
        addComponent(tf);
        type = new NativeSelect();
        type.setNullSelectionAllowed(false);
        type.addContainerProperty(CAPTION, String.class, "");
        type.setItemCaptionPropertyId(CAPTION);
        type.addItem(Notification.TYPE_HUMANIZED_MESSAGE)
                .getItemProperty(CAPTION).setValue("Humanized");
        type.addItem(Notification.TYPE_ERROR_MESSAGE).getItemProperty(CAPTION)
                .setValue("Error");
        type.addItem(Notification.TYPE_WARNING_MESSAGE)
                .getItemProperty(CAPTION).setValue("Warning");
        type.addItem(Notification.TYPE_TRAY_NOTIFICATION)
                .getItemProperty(CAPTION).setValue("Tray");
        type.setValue(type.getItemIds().iterator().next());
        addComponent(type);
        Button showNotification = new Button("Show notification", this);
        addComponent(showNotification);
    }

    @Override
    protected String getDescription() {
        return "Generic test case for notifications";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    public void buttonClick(ClickEvent event) {
        Notification n = new Notification((String) tf.getValue(),
                (Integer) type.getValue());
        event.getButton().getWindow().showNotification(n);

    }
}
