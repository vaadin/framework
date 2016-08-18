package com.vaadin.tests.tickets;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.SystemError;
import com.vaadin.ui.LegacyWindow;

public class Ticket1663 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {

        LegacyWindow main = new LegacyWindow("#1663");
        setMainWindow(main);

        LegacyTextField tf = new LegacyTextField("First name");
        tf.setDescription(
                "The first name is used for the administration user interfaces only.");
        tf.setComponentError(
                new SystemError("You must enter only one first name."));

        main.addComponent(tf);
    }
}
