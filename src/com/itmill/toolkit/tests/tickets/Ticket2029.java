package com.itmill.toolkit.tests.tickets;

import java.util.Random;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2029 extends Application {

    Random r = new Random();

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        Panel p = new Panel("1000x150");
        p.setWidth("1000");
        p.setHeight("150");

        OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        p.setLayout(layout);
        p.getLayout().setSizeFull();

        w.getLayout().addComponent(p);

        for (int i = 0; i < 10; i++) {
            TextField tf = new TextField();
            if (r.nextBoolean()) {
                tf.setCaption("Caption");
            }
            if (r.nextBoolean()) {
                tf.setRequired(true);
            }
            if (r.nextBoolean()) {
                tf.setComponentError(new UserError("Error"));
            }
            tf.setWidth("100%");
            layout.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
            p.addComponent(tf);

        }
    }
}
