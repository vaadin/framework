package com.vaadin.tests.components.notification;

import com.vaadin.data.Item;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.NotificationConfiguration;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * Test UI for different roles of Notifications.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class NotificationsWaiAria extends AbstractTestUI {

    private static final String CAPTION = "CAPTION";

    private TextField prefix;
    private TextField postfix;
    private NativeSelect role;

    private TextArea tf;
    private ComboBox type;

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        prefix = new TextField("Prefix", "Info");
        addComponent(prefix);

        postfix = new TextField("Postfix",
                " - closes automatically after 10 seconds");
        addComponent(postfix);

        role = new NativeSelect("NotificationRole");
        role.addItem(NotificationRole.ALERT);
        role.addItem(NotificationRole.STATUS);
        role.setValue(role.getItemIds().iterator().next());
        addComponent(role);

        tf = new TextArea("Text", "Hello world");
        tf.setRows(10);
        addComponent(tf);
        type = new ComboBox();
        type.setNullSelectionAllowed(false);
        type.addContainerProperty(CAPTION, String.class, "");

        type.setItemCaptionPropertyId(CAPTION);

        Item item = type.addItem(Notification.Type.HUMANIZED_MESSAGE);
        item.getItemProperty(CAPTION).setValue("Humanized");

        item = type.addItem(Notification.Type.ERROR_MESSAGE);
        item.getItemProperty(CAPTION).setValue("Error");

        item = type.addItem(Notification.Type.WARNING_MESSAGE);
        item.getItemProperty(CAPTION).setValue("Warning");

        item = type.addItem(Notification.Type.TRAY_NOTIFICATION);
        item.getItemProperty(CAPTION).setValue("Tray");

        item = type.addItem(Notification.Type.ASSISTIVE_NOTIFICATION);
        item.getItemProperty(CAPTION).setValue("Assistive");

        type.setValue(type.getItemIds().iterator().next());
        addComponent(type);

        Button showNotification = new Button("Show notification",
                new SettingHandler());
        addComponent(showNotification);

        Button showDefaultNotification = new Button("Default notification",
                new DefaultHandler());
        addComponent(showDefaultNotification);
    }

    @Override
    protected String getTestDescription() {
        return "Generic test case for notifications";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private class SettingHandler implements ClickListener {
        @Override
        public void buttonClick(ClickEvent event) {
            Type typeValue = (Type) type.getValue();

            Notification n = new Notification(tf.getValue(), typeValue);
            n.setDelayMsec(-1);
            n.setHtmlContentAllowed(true);
            NotificationConfiguration notificationConf = UI.getCurrent()
                    .getNotificationConfiguration();
            notificationConf.setAssistivePrefix(typeValue, prefix.getValue());
            notificationConf.setAssistivePostfix(typeValue, postfix.getValue());
            notificationConf.setAssistiveRole(typeValue,
                    (NotificationRole) role.getValue());

            n.show(Page.getCurrent());
        }
    }

    private class DefaultHandler implements ClickListener {
        @Override
        public void buttonClick(ClickEvent event) {
            Notification n = new Notification(tf.getValue(),
                    (Type) type.getValue());
            n.setHtmlContentAllowed(true);
            n.show(Page.getCurrent());
        }
    }

}
