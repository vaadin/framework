package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;

public class Ticket2283 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeUndefined();

        gl.addComponent(new Label(
                "Label 1 abc abc abcasdfas dfasd fasdf asdf sadf asdf"));
        gl.addComponent(new Label("Label 2 abc abc abc "));
        gl.addComponent(new Label("Colspan2, align right"), 0, 1, 1, 1);
        gl.setComponentAlignment(gl, AlignmentHandler.ALIGNMENT_RIGHT,
                AlignmentHandler.ALIGNMENT_TOP);
        w.setLayout(gl);

    }

}
