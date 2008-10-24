package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2181 extends Application implements Button.ClickListener {

    Window main = new Window("#2181 test");
    TextField tf = new TextField("Test field");
    Button b = new Button("Press to break down", this);

    public void init() {
        setMainWindow(main);
        main.addComponent(tf);
        main.addComponent(b);
    }

    public void buttonClick(ClickEvent event) {
        tf.setComponentError(new UserError("Noooo... "));
    }
}
