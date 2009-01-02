package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.terminal.SystemError;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1663 extends com.itmill.toolkit.Application {

    @Override
    public void init() {

        Window main = new Window("#1663");
        setMainWindow(main);

        TextField tf = new TextField("First name");
        tf
                .setDescription("The first name is used for the administration user interfaces only.");
        tf.setComponentError(new SystemError(
                "You must enter only one first name."));

        main.addComponent(tf);
    }
}
