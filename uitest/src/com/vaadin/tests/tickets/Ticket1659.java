package com.vaadin.tests.tickets;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;

public class Ticket1659 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow mainWin = new LegacyWindow();
        setMainWindow(mainWin);
        mainWin.addComponent(new Button(
                "Change URI using Application.getURL()",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        mainWin.open(new ExternalResource(getURL() + "#"
                                + System.currentTimeMillis()));
                    }
                }));
        mainWin.addComponent(new Button("Change URI uring Window.getURL()",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        mainWin.open(new ExternalResource(mainWin.getURL()
                                + "#" + System.currentTimeMillis()));
                    }
                }));
    }

}
