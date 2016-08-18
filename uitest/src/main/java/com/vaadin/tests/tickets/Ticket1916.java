package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.ui.LegacyTextField;

public class Ticket1916 extends LegacyApplication {

    @Override
    public void init() {

        HorizontalLayout test = new HorizontalLayout();
        test.setSizeFull();

        LegacyTextField tf = new LegacyTextField();
        tf.setComponentError(new UserError("Error message"));

        test.addComponent(tf);
        test.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        LegacyWindow w = new LegacyWindow("Test #1916", test);
        setMainWindow(w);
    }

}
