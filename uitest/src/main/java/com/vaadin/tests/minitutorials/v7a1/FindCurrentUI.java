package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Finding%20the%20current
 * %20Root%20and%20Application
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class FindCurrentUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Button helloButton = new Button("Say Hello");
        helloButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String msg = "Running in ";
                msg += VaadinSession.getCurrent().getConfiguration()
                        .isProductionMode() ? "production" : "debug";
                Notification.show(msg);
            }
        });

        helloButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("This UI is "
                        + UI.getCurrent().getClass().getSimpleName());
            }
        });

        setContent(helloButton);
    }

}
