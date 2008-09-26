package com.itmill.toolkit.tests.tickets;

import java.util.Date;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

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
