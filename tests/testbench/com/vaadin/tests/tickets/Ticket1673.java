package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class Ticket1673 extends com.vaadin.Application {

    @Override
    public void init() {

        final Window main = new Window("#1673");
        setMainWindow(main);

        main.addComponent(new Button("close", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                close();
            }
        }));

    }

    public static Application.SystemMessages getSystemMessages() {
        Application.CustomizedSystemMessages msgs = new Application.CustomizedSystemMessages();

        msgs.setSessionExpiredURL("http://www.vaadin.com/");
        msgs.setSessionExpiredCaption("Foo");
        msgs.setSessionExpiredMessage("Bar");

        return msgs;
    }
}
