package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

public class VaadinSessionAttribute extends AbstractReindeerTestUI {

    private static final String ATTR_NAME = "myAttribute";

    @Override
    protected void setup(VaadinRequest request) {
        getSession().setAttribute(ATTR_NAME, Integer.valueOf(42));
        getSession().setAttribute(Integer.class, Integer.valueOf(42 * 2));

        addComponent(
                new Button("Show attribute values", event -> {
                    Notification notification = new Notification(
                            getSession().getAttribute(ATTR_NAME) + " & "
                                    + getSession().getAttribute(Integer.class));
                    notification.setDelayMsec(Notification.DELAY_FOREVER);
                    notification.show(getPage());
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Test to verify that session attributes are saved between requests.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9514);
    }

}
