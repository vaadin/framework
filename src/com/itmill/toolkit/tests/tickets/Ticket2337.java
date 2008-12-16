package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2337 extends Application {

    GridLayout gl = new GridLayout(3, 1);

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
