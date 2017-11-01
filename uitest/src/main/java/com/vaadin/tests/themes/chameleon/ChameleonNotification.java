package com.vaadin.tests.themes.chameleon;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.themes.ChameleonTheme;

@Theme(ChameleonTheme.THEME_NAME)
public class ChameleonNotification extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {

        addButton("Notification", event -> {
            Notification notification = new Notification("Notification");
            notification.setDelayMsec(30000);
            notification.show(getUI().getPage());
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 15351;
    }
}
