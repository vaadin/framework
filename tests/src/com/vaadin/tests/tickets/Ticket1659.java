package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1659 extends Application {

    @Override
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
