package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class Ticket1916 extends Application.LegacyApplication {

    @Override
    public void init() {

        HorizontalLayout test = new HorizontalLayout();
        test.setSizeFull();

        TextField tf = new TextField();
        tf.setComponentError(new UserError("Error message"));

        test.addComponent(tf);
        test.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        Root w = new Root("Test #1916", test);
        setMainWindow(w);
    }

}
