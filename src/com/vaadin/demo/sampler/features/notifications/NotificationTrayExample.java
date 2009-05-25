package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class NotificationTrayExample extends VerticalLayout {

    public NotificationTrayExample() {
        setSpacing(true);
        setWidth(null); // layout will grow with content

        final TextField caption = new TextField("Caption", "New message");
        caption.setWidth("200px");
        addComponent(caption);

        final TextField description = new TextField("Description",
                "<b>John:</b> Could you upload Invoices-2008.csv so that...");
        description.setWidth("300px");
        addComponent(description);

        Button show = new Button("Show notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification(
                                (String) caption.getValue(),
                                (String) description.getValue(),
                                Notification.TYPE_TRAY_NOTIFICATION);

                    }
                });
        addComponent(show);
        setComponentAlignment(show, Alignment.MIDDLE_RIGHT);

    }
}
