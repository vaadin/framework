package com.vaadin.tests.tickets;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;

public class Ticket1673 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("#1673");
        setMainWindow(main);

        main.addComponent(new Button("close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        }));

    }

    public static SystemMessages getSystemMessages() {
        CustomizedSystemMessages msgs = new CustomizedSystemMessages();

        msgs.setSessionExpiredURL("http://www.vaadin.com/");
        msgs.setSessionExpiredCaption("Foo");
        msgs.setSessionExpiredMessage("Bar");

        return msgs;
    }
}
