package com.vaadin.tests.tickets;

import com.vaadin.terminal.SystemError;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class Ticket1663 extends com.vaadin.Application.LegacyApplication {

    @Override
    public void init() {

        Root main = new Root("#1663");
        setMainWindow(main);

        TextField tf = new TextField("First name");
        tf.setDescription("The first name is used for the administration user interfaces only.");
        tf.setComponentError(new SystemError(
                "You must enter only one first name."));

        main.addComponent(tf);
    }
}
