package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;
import com.vaadin.ui.Window;

public class Ticket2526 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root main = new Root();
        setMainWindow(main);
        Button b = new Button("Add windows");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                main.addWindow(new Window());
            }
        });
        main.addComponent(b);
    }
}
