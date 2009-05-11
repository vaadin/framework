package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1916 extends Application {

    @Override
    public void init() {

        OrderedLayout test = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        test.setSizeFull();

        TextField tf = new TextField();
        tf.setComponentError(new UserError("Error message"));

        test.addComponent(tf);
        test.setComponentAlignment(tf,
                OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

        Window w = new Window("Test #1916", test);
        setMainWindow(w);
    }

}
