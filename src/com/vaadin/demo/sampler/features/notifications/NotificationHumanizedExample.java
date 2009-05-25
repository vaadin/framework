package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class NotificationHumanizedExample extends VerticalLayout {

    public NotificationHumanizedExample() {
        setSpacing(true);
        setWidth(null); // layout will grow with content

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
        setComponentAlignment(show, Alignment.MIDDLE_RIGHT);

    }
}
