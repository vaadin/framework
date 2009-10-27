package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket2337 extends Application {

    GridLayout gl = new GridLayout(3, 1);

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        Button b = new Button("add", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                gl.addComponent(new Label("asd"), 0, gl.getCursorY(), 2, gl
                        .getCursorY());

            }

        });
        w.addComponent(b);

        b = new Button("empty", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                gl.removeAllComponents();
                ;

            }

        });
        w.addComponent(b);

        w.addComponent(gl);

    }

}
