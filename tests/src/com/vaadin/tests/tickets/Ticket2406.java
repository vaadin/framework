package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2406 extends Application {

    private Window w;

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((VerticalLayout) w.getLayout());
    }

    private void createUI(VerticalLayout layout) {
        w = new Window("A sub window");
        w.setSizeUndefined();
        getMainWindow().addWindow(w);

        VerticalLayout l = new VerticalLayout();
        l.setSizeFull();
        w.setLayout(l);

        Button b = new Button("Button 1");
        b.setSizeFull();
        b.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                w.setHeight("200px");
            }

        });
        l.addComponent(b);

        for (int i = 0; i < 5; i++) {
            b = new Button("Button number " + (i + 2));
            b.setSizeFull();
            l.addComponent(b);
        }
    }
}
