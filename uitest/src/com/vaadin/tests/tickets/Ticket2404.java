package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2404 extends LegacyApplication {

    @Override
    public void init() {

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeFull();

        Button bb = new Button("1st row on 2x2 GridLayout");
        bb.setSizeFull();
        gl.addComponent(bb, 0, 0, 1, 0);
        for (int i = 0; i < 2; i++) {
            Button b = new Button("" + i);
            gl.addComponent(b);
            b.setSizeFull();
        }

        setMainWindow(new LegacyWindow("GridLayout test", gl));

    }
}
