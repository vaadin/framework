package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2283 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeUndefined();

        gl.addComponent(new Label(
                "Label 1 abc abc abcasdfas dfasd fasdf asdf sadf asdf"));
        gl.addComponent(new Label("Label 2 abc abc abc "));
        Label l = new Label("Colspan2, align right");
        gl.addComponent(l, 0, 1, 1, 1);
        gl.setComponentAlignment(l, Alignment.TOP_RIGHT);
        w.setContent(gl);

    }

}
