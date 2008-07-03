package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1902 extends Application {

    public void init() {

        OrderedLayout lo = new OrderedLayout();
        setMainWindow(new Window("Testcase for #1902", lo));

        lo.setSpacing(false);
        lo.setMargin(false);

        TextField tf = new TextField("100% wide textfield");
        lo.addComponent(tf);
        tf.setWidth("100%");

        Button b = new Button("100% wide button");
        lo.addComponent(b);
        b.setWidth("100%");

    }
}