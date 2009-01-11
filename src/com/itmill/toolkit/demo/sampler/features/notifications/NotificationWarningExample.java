package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.Notification;

public class NotificationWarningExample extends OrderedLayout {

    public NotificationWarningExample() {
        setSpacing(true);

        final TextField caption = new TextField("Caption", "Upload canceled");
        caption.setWidth("200px");
        addComponent(caption);

        final TextField description = new TextField("Description",
                "Invoices-2008.csv will not be processed");
        description.setWidth("300px");
        addComponent(description);

        Button show = new Button("Show notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification(
                                (String) caption.getValue(),
                                (String) description.getValue(),
                                Notification.TYPE_WARNING_MESSAGE);

                    }
                });
        addComponent(show);
        setComponentAlignment(show, Alignment.MIDDLE_RIGHT);

    }
}
