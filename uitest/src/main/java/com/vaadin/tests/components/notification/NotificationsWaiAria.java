package com.vaadin.tests.components.notification;

import java.util.LinkedHashMap;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.NotificationConfiguration;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextArea;

/**
 * Test UI for different roles of Notifications.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class NotificationsWaiAria extends AbstractReindeerTestUI {

    private static final String CAPTION = "CAPTION";

    private TextField prefix;
    private TextField postfix;
    private NativeSelect role;

    private TextArea tf;
    private ComboBox<Notification.Type> type;

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        prefix = new TextField("Prefix", "Info");
        // The text fields need to be non-immediate to avoid an extra event that
        // hides the notification while the test is still trying to read its
        // contents.
        prefix.setValueChangeMode(ValueChangeMode.BLUR);
        addComponent(prefix);

        postfix = new TextField("Postfix",
                " - closes automatically after 10 seconds");
        postfix.setValueChangeMode(ValueChangeMode.BLUR);
        addComponent(postfix);

        role = new NativeSelect("NotificationRole");
        role.addItem(NotificationRole.ALERT);
        role.addItem(NotificationRole.STATUS);
        role.setValue(role.getItemIds().iterator().next());
        addComponent(role);

        tf = new TextArea("Text", "Hello world");
        tf.setImmediate(false);
        tf.setRows(10);
        addComponent(tf);
        type = new ComboBox<>();
        LinkedHashMap<Notification.Type, String> items = new LinkedHashMap<>();
        items.put(Notification.Type.HUMANIZED_MESSAGE, "Humanized");
        items.put(Notification.Type.ERROR_MESSAGE, "Error");
        items.put(Notification.Type.WARNING_MESSAGE, "Warning");
        items.put(Notification.Type.TRAY_NOTIFICATION, "Tray");
        items.put(Notification.Type.ASSISTIVE_NOTIFICATION, "Assistive");

        type.setItemCaptionGenerator(item -> items.get(item));
        type.setItems(items.keySet());

        type.setValue(items.keySet().iterator().next());
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
            Type typeValue = type.getValue();

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
            Notification n = new Notification(tf.getValue(), type.getValue());
            n.setHtmlContentAllowed(true);
            n.show(Page.getCurrent());
        }
    }

}
