package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2267 extends Application {

    Label l = new Label("0");

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
