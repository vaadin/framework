package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2337 extends LegacyApplication {

    GridLayout gl = new GridLayout(3, 1);

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);
        Button b = new Button("add", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                gl.addComponent(new Label("asd"), 0, gl.getCursorY(), 2,
                        gl.getCursorY());

            }

        });
        w.addComponent(b);

        b = new Button("empty", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                gl.removeAllComponents();

            }

        });
        w.addComponent(b);

        w.addComponent(gl);

    }

}
