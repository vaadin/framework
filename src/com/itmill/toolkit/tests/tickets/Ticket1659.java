package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1659 extends Application {

    public void init() {
        final Window mainWin = new Window();
        setMainWindow(mainWin);
        mainWin.addComponent(new Button(
                "Change URI using Application.getURL()",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        mainWin.open(new ExternalResource(getURL() + "#"
                                + System.currentTimeMillis()));
                    }
                }));
        mainWin.addComponent(new Button("Change URI uring Window.getURL()",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        mainWin.open(new ExternalResource(mainWin.getURL()
                                + "#" + System.currentTimeMillis()));
                    }
                }));
    }

}
