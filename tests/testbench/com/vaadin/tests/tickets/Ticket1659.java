package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

public class Ticket1659 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root mainWin = new Root();
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
                        mainWin.open(new ExternalResource(getWindowUrl(mainWin)
                                + "#" + System.currentTimeMillis()));
                    }
                }));
    }

}
