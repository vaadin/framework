package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.AlignmentHandler;

public class Ticket2283 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeUndefined();

        gl.addComponent(new Label(
                "Label 1 abc abc abcasdfas dfasd fasdf asdf sadf asdf"));
        gl.addComponent(new Label("Label 2 abc abc abc "));
        Label l = new Label("Colspan2, align right");
        gl.addComponent(l, 0, 1, 1, 1);
        gl.setComponentAlignment(l, AlignmentHandler.ALIGNMENT_RIGHT,
                AlignmentHandler.ALIGNMENT_TOP);
        w.setLayout(gl);

    }

}
