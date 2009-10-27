package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2267 extends Application {

    Label l = new Label("0");

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout gl = new GridLayout(4, 2);

        Button button = new Button("1", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                Button b = event.getButton();
                l.setValue(l.getValue() + b.getCaption());

            }

        });

        gl.addComponent(l, 0, 0, 3, 0);
        gl.addComponent(button);
        gl.addComponent(new Label("2"));
        gl.addComponent(new Label("3"));
        gl.addComponent(new Label("4"));

        w.setLayout(gl);

    }
}
