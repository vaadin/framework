package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket2526 extends Application {

    @Override
    public void init() {
        final Window main = new Window();
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
