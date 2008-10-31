package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class NotificationHumanizedExample extends OrderedLayout {

    public NotificationHumanizedExample() {
        setSpacing(true);

        final TextField caption = new TextField("Caption", "Document saved");
        caption.setWidth("200px");
        addComponent(caption);

        final TextField description = new TextField("Description",
                "Invoices-2008.csv");
        description.setWidth("300px");
        addComponent(description);

        Button show = new Button("Show notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification(
                                (String) caption.getValue(),
                                (String) description.getValue());

                    }
                });
        addComponent(show);
        setComponentAlignment(show, ALIGNMENT_RIGHT, ALIGNMENT_VERTICAL_CENTER);

    }
}
