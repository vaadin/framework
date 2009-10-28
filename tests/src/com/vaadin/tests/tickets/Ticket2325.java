package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2325 extends Application {

    @Override
    public void init() {
        Window main = new Window("Testing....");
        setMainWindow(main);

        final VerticalLayout lo = new VerticalLayout();
        lo.setSizeUndefined();
        lo.setWidth("100%");
        TextField tf = new TextField();
        tf.setValue("The textfield should fill the window."
                + "\n - Try to resize window\n - Try to push REdo button");
        tf.setRows(10);
        tf.setWidth("100%");
        lo.addComponent(tf);
        Window subWin = new Window(
                "This window should initially be as wide as the caption", lo);
        main.addWindow(subWin);
        // subWin.setWidth("500px");
    }
}
