package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Window;

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
