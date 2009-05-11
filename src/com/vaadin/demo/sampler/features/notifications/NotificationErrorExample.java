package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class NotificationErrorExample extends VerticalLayout {

    public NotificationErrorExample() {
        setSpacing(true);
        setWidth(null); // layout will grow with content

        final TextField caption = new TextField("Caption", "Upload failed");
        caption.setWidth("200px");
        addComponent(caption);

        final TextField description = new TextField(
                "Description",
                "Invoices-2008.csv could not be read.<br/>"
                        + "Perhaps the file is damaged, or in the wrong format?<br/>"
                        + "Try re-exporting and uploading the file again.");
        description.setWidth("300px");
        addComponent(description);

        Button show = new Button("Show notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification(
                                (String) caption.getValue(),
                                (String) description.getValue(),
                                Notification.TYPE_ERROR_MESSAGE);

                    }
                });
        addComponent(show);
        setComponentAlignment(show, Alignment.MIDDLE_RIGHT);

    }
}
