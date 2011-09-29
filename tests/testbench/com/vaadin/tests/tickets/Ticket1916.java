package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1916 extends Application {

    @Override
    public void init() {

        HorizontalLayout test = new HorizontalLayout();
        test.setSizeFull();

        TextField tf = new TextField();
        tf.setComponentError(new UserError("Error message"));

        test.addComponent(tf);
        test.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        Window w = new Window("Test #1916", test);
        setMainWindow(w);
    }

}
