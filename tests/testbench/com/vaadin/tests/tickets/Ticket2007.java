package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

public class Ticket2007 extends Application.LegacyApplication {

    int childs = 0;

    @Override
    public void init() {

        final Root main = new Root("Main window for #2007");
        setMainWindow(main);
        main.addComponent(new Button("Open another (non-main) window",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Root c = new Root("Non-main browser window "
                                + (++childs));
                        addWindow(c);
                        main.open(new ExternalResource(getWindowUrl(c)), "_new");
                    }
                }));
    }
}
