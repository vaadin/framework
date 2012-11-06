package com.vaadin.tests.tickets;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;

public class Ticket2007 extends LegacyApplication {

    int childs = 0;

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("Main window for #2007");
        setMainWindow(main);
        main.addComponent(new Button("Open another (non-main) window",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        LegacyWindow c = new LegacyWindow(
                                "Non-main browser window " + (++childs));
                        addWindow(c);
                        main.open(new ExternalResource(c.getURL()), "_new");
                    }
                }));
    }
}
