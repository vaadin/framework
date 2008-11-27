package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2232 extends Application {

    @Override
    public void init() {
        setMainWindow(new Window());
        setTheme("tests-tickets");

        getMainWindow()
                .addComponent(
                        new Label(
                                "Defining spacing must be possible also with pure CSS"));

        Layout.SpacingHandler gl;
        gl = new OrderedLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setStyleName("t2232");
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setStyleName("t2232");
        fillAndAdd(gl);

        gl = new OrderedLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setSpacing(true);
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setSpacing(true);
        fillAndAdd(gl);

        gl = new OrderedLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        fillAndAdd(gl);

    }

    private void fillAndAdd(Layout.SpacingHandler gl) {
        for (int i = 0; i < 4; i++) {
            Button b = new Button("B");
            b.setSizeFull();
            gl.addComponent(b);
        }
        String caption = gl.getClass().getSimpleName();
        caption += " style: " + gl.getStyleName() + ", spacingFromServer:"
                + gl.isSpacingEnabled();
        gl.setCaption(caption);
        getMainWindow().addComponent(gl);
    }

}
