package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.SpacingHandler;

public class Ticket2232 extends Application {

    @Override
    public void init() {
        setMainWindow(new Window());
        setTheme("tests-tickets");

        getMainWindow()
                .addComponent(
                        new Label(
                                "Defining spacing must be possible also with pure CSS"));

        Layout gl;
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
        ((SpacingHandler) gl).setSpacing(true);
        fillAndAdd(gl);

        gl = new GridLayout();
        gl.setWidth("100%");
        gl.setHeight("200px");
        ((SpacingHandler) gl).setSpacing(true);
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

    private void fillAndAdd(Layout gl) {
        for (int i = 0; i < 4; i++) {
            Button b = new Button("B");
            b.setSizeFull();
            gl.addComponent(b);
        }
        String caption = gl.getClass().getSimpleName();
        caption += " style: " + gl.getStyleName() + ", spacingFromServer:"
                + ((SpacingHandler) gl).isSpacingEnabled();
        gl.setCaption(caption);
        getMainWindow().addComponent(gl);
    }

}
