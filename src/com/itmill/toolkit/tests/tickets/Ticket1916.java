package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1916 extends Application {

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
