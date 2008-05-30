package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;

public class Ticket1673 extends com.itmill.toolkit.Application {

    public void init() {

        final Window main = new Window("#1673");
        setMainWindow(main);

        main.addComponent(new Button("close", this, "close"));

    }

    public static Application.SystemMessages getSystemMessages() {
        Application.CustomizedSystemMessages msgs = new Application.CustomizedSystemMessages();

        msgs.setSessionExpiredURL("http://www.itmill.com/");
        msgs.setSessionExpiredCaption("Foo");
        msgs.setSessionExpiredMessage("Bar");

        return msgs;
    }
}
