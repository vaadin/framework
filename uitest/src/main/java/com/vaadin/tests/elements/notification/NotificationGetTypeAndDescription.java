package com.vaadin.tests.elements.notification;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@Widgetset("com.vaadin.DefaultWidgetSet")
@PreserveOnRefresh
public class NotificationGetTypeAndDescription extends AbstractTestUIWithLog {

    private static final Type[] types = { Type.WARNING_MESSAGE,
            Type.ERROR_MESSAGE, Type.HUMANIZED_MESSAGE,
            Type.TRAY_NOTIFICATION };
    public static final String[] type_names = { "warning", "error", "humanized",
            "tray_notification" };
    public static final String[] captions = { "warningC", "errorC",
            "humanizedC", "tray_notificationC" };
    public static final String[] descriptions = { "warning", "error",
            "humanized", "tray_notification" };

    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < types.length; i++) {
            Button btn = new Button();
            btn.setId("button" + i);
            btn.setCaption(type_names[i]);
            btn.addClickListener(new CounterClickListener(i));
            addComponent(btn);
        }
        // add extra button which shows Notification only with caption #14356
        Button btn = new Button("Show notification");
        btn.setId("showid");
        btn.addClickListener(event -> Notification.show("test"));
        addComponent(btn);

        Button hide = new Button("Hide all notifications");
        hide.setId("hide");
        hide.addClickListener(event -> {
            List<Notification> notifications = new ArrayList<>();
            getAllChildrenIterable(getUI()).forEach(conn -> {
                if (conn instanceof Notification) {
                    notifications.add((Notification) conn);
                }
            });
            notifications.forEach(Notification::close);
        });
        addComponent(hide);
    }

    @Override
    protected String getTestDescription() {
        return "Test getType and getDescription methods of NotificationElement";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13768;
    }

    private class CounterClickListener implements ClickListener {
        int index;

        public CounterClickListener(int i) {
            index = i;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            Notification n = Notification.show(captions[index],
                    descriptions[index], types[index]);
            n.addCloseListener(e -> {
                log("Notification (" + descriptions[index] + ") closed "
                        + (e.isUserOriginated() ? "by user" : "from server"));
            });
        }

    }
}
