package com.vaadin.tests.tickets;

import java.util.Date;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket2106 extends Application {

    private static CustomizedSystemMessages msgs = new Application.CustomizedSystemMessages();
    static {
        // We will forward the user to www.itmill.com when the session expires
        msgs.setSessionExpiredURL("http://www.itmill.com");
        msgs.setSessionExpiredMessage(null);
        msgs.setSessionExpiredCaption(null);
    }

    public static Application.SystemMessages getSystemMessages() {
        return msgs;
    }

    @Override
    public void init() {
        setMainWindow(new Window("#2106"));
        getMainWindow().addComponent(
                new Button("Do nothing", new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().addComponent(
                                new Label("Last time did nothing: "
                                        + new Date()));
                    }
                }));
    }

}
