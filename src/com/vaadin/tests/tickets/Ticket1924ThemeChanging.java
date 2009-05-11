package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket1924ThemeChanging extends com.vaadin.Application {

    private Label l = new Label("Background should be red with test theme");

    @SuppressWarnings("unused")
    private Panel p;

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        l.setStyleName("red");
        main.addComponent(l);

        Button b = new Button("Toggle tests-tickets theme");
        b.addListener(new ClickListener() {
            boolean flag = false;

            public void buttonClick(ClickEvent event) {
                if (flag = !flag) {
                    main.setTheme("tests-tickets");
                } else {
                    main.setTheme(null);
                }
            }
        });

        main.addComponent(b);

        b = new Button("Modify caption (should not reload page)");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                main.setCaption(main.getCaption() + ".");
            }
        });

        main.addComponent(b);

    }
}