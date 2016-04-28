package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2232 extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new LegacyWindow());
        setTheme("tests-tickets");

        getMainWindow()
                .addComponent(
                        new Label(
                                "Defining spacing must be possible also with pure CSS"));

        Layout gl;
        gl = new VerticalLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setStyleName("t2232");
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        gl.setStyleName("t2232");
        fillAndAdd(gl);

        gl = new VerticalLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        ((SpacingHandler) gl).setSpacing(true);
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        ((SpacingHandler) gl).setSpacing(true);
        fillAndAdd(gl);

        gl = new VerticalLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        fillAndAdd(gl);

    }

    private void fillAndAdd(Layout gl) {
        for (int i = 0; i < 4; i++) {
            Button b = new Button("B");
            b.setSizeFull();
            gl.addComponent(b);
        }
        String caption = gl.getClass().getSimpleName();
        caption += " style: " + gl.getStyleName() + ", spacingFromServer:"
                + ((SpacingHandler) gl).isSpacing();
        gl.setCaption(caption);
        getMainWindow().addComponent(gl);
    }

}
