package com.vaadin.tests.layouts;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;

public class GridLayoutInsidePanel2 extends Application {

    private Layout layout;

    @Override
    public void init() {
        Window w = new Window("Main");
        setMainWindow(w);
        layout = w.getLayout();
        GridLayout gl = new GridLayout(1, 1);
        gl.setSizeUndefined();
        Label l = new Label("This should be visible");
        l.setWidth("100px");
        gl.addComponent(l);

        layout.setSizeUndefined();
        layout.addComponent(gl);
    }

}
