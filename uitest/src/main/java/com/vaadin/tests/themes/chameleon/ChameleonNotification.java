package com.vaadin.tests.themes.chameleon;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ChameleonTheme;

@Theme(ChameleonTheme.THEME_NAME)
public class ChameleonNotification extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {

        addButton("Notification", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Notification notification = new Notification("Notification");
                notification.setDelayMsec(30000);
                notification.show(getUI().getPage());
            }
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 15351;
    }
}
