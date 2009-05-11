package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1775 extends com.vaadin.Application {

    @Override
    public void init() {

        final Window main = new Window("#1775");
        setMainWindow(main);
        main.setTheme("example");
        String layoutName = "mainLayout";
        final CustomLayout layout = new CustomLayout(layoutName);

        main.addComponent(layout);

        Button button2 = new Button("Populate content");
        main.addComponent(button2);

        final Button button = new Button("Change content");
        main.addComponent(button);

        button2.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                Label mainComponent = new Label("Main");
                Label header = new Label("Header");
                final Label anotherComponent = new Label("another");
                layout.addComponent(mainComponent, "body");
                layout.addComponent(header, "loginUser");
                button.addListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        layout.addComponent(anotherComponent, "body");
                        layout.removeComponent("loginUser");
                    }
                });

            }
        });

    }

}
